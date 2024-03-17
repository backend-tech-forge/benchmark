package org.benchmarker.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.beans.RequestCounter;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.template.controller.dto.TempSaveTestResultDto;
import org.benchmarker.template.controller.dto.TestResultResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.model.*;
import org.benchmarker.template.repository.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.benchmarker.template.common.TemplateUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultService extends AbstractTestResultService {

    private final TestTemplateRepository testTemplateRepository;

    private final TestResultRepository testResultRepository;

    private final TestTpsRepository testTpsRepository;

    private final TestMttfbRepository testMttfbRepository;

    private final TestErrorLogRepository testErrorLogRepository;

    private final WebClient webClient;

    private final RequestCounter requestCounter;

    @Override
    public TestResultResponseDto measurePerformance(Integer templateId) throws InterruptedException {

        // 템플릿이 존재하는지 먼저 파악.
        TestTemplate testTemplate = testTemplateRepository.findById(templateId)
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        List<TempSaveTestResultDto> test = Flux.range(0, testTemplate.getVuser())
                .flatMap(i -> createAndProcessRequest(testTemplate))
                .collectList()
                .block();


        return test;
    }

    private Mono<TempSaveTestResultDto> createAndProcessRequest(TestTemplate testTemplate) {

        long startTime = System.currentTimeMillis();
        Mono<ResponseEntity<String>> resultMono = createRequest(webClient, testTemplate);

        return resultMono.publishOn(Schedulers.boundedElastic()).flatMap(response -> {
            long endTime = System.currentTimeMillis();

            HttpStatusCode statusCode = response.getStatusCode();
            requestCounter.updateTotalResult(statusCode);

            int totalRequests = requestCounter.getTotalRequests();
            int totalSuccesses = requestCounter.getTotalSuccesses();
            int totalErrors = requestCounter.getTotalErrors();

            double tpsAvgTime = calculateTPS(startTime, endTime, totalRequests);
            double avgResponseTime = calculateAvgResponseTime(startTime, endTime, totalRequests);

            TempSaveTestResultDto tempSaveTestResultDto = TempSaveTestResultDto.builder()
                    .startAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
                    .finishAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()))
                    .success(totalSuccesses)
                    .error(totalErrors)
                    .statusCode(statusCode.value())
                    .tpsAvg(tpsAvgTime)
                    .mttbfbAvg(avgResponseTime)
                    .build();

//            TestResult testResult = TestResult.builder()
//                    .testTemplate(testTemplate)
//                    .totalRequest(totalRequests)
//                    .totalSuccess(totalSuccesses)
//                    .totalError(totalErrors)
//                    .tpsAvg(tpsAvgTime)
//                    .mttbfbAvg(avgResponseTime)
//                    .build();
//
//            TestResult saveTestResult = testResultRepository.save(testResult);
//
//            TestTps testTps = TestTps.builder()
//                    .testResult(saveTestResult)
//                    .startAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
//                    .transaction(tpsAvgTime)
//                    .finishAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()))
//                    .build();
//
//            testTpsRepository.save(testTps);
//
//            long networkDelay = (long) (endTime - startTime - avgResponseTime);
//            double mttfb = avgResponseTime - networkDelay;
//            TestMttfb testMttfb = TestMttfb.builder()
//                    .testResult(saveTestResult)
//                    .mttfb(mttfb)
//                    .startAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
//                    .finishAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()))
//                    .build();
//            testMttfbRepository.save(testMttfb);


            return Mono.just(tempSaveTestResultDto);

        }).onErrorResume(throwable -> {
            log.error("Error occurred: " + throwable.getMessage());


            requestCounter.incrementTotalRequests();
            requestCounter.incrementTotalErrors();

            TestErrorLog testErrorLog = TestErrorLog.builder()
                    .message(throwable.getMessage())
                    .build();

            testErrorLogRepository.save(testErrorLog);
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        });
    }

    @Override
    public TestTemplateResponseDto getTemplateResult(Integer templateResultId) {

        TestTemplate testTemplate = testTemplateRepository.findById(templateResultId)
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        return testTemplate.convertToResponseDto();
    }

    @Override
    public List<TestResultResponseDto> getTemplates(String groupId) {



        return null;
    }
}
