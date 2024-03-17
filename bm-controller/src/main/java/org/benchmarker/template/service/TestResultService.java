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
import java.util.*;

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

        // 임시 생성 >> tps, mttbfb 저장할 때 필요한 key 생성
        TestResult tempResult = TestResult.builder()
                .testTemplate(testTemplate)
                .build();
        TestResult testResult = testResultRepository.save(tempResult);

        // 비동기 처리 후 각각의 결과를 담은 객체 반환
        List<TempSaveTestResultDto> saveTestResults = Flux.range(0, testTemplate.getVuser())
                .flatMap(i -> createAndProcessRequest(testTemplate, testResult))
                .collectList()
                .block();

        // 시간순으로 정렬
        Collections.sort(saveTestResults, Comparator.comparing(TempSaveTestResultDto::getStartAt));

        int totalRequest = saveTestResults.size();
        int totalSuccess = 0;
        int totalError = 0;

        Map<String, Integer> statusCodeCount = new HashMap<>();
        Map<String, Double> mttfbPercentiles = new HashMap<>();
        Map<String, Double> tpsPercentiles = new HashMap<>();

        long curAvgStartAt = saveTestResults.get(0).getStartAt();

        for (int i = 0; i < saveTestResults.size(); i++) {
            totalSuccess += saveTestResults.get(i).getSuccess();
            totalError += saveTestResults.get(i).getError();

            int statusCode = saveTestResults.get(i).getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                statusCodeCount.put("2xx", statusCodeCount.getOrDefault("2xx", 0) + 1);
            } else if (statusCode >= 300 && statusCode < 400) {
                statusCodeCount.put("3xx", statusCodeCount.getOrDefault("3xx", 0) + 1);
            } else if (statusCode >= 400 && statusCode < 500) {
                statusCodeCount.put("4xx", statusCodeCount.getOrDefault("4xx", 0) + 1);
            } else if (statusCode >= 500 && statusCode < 600) {
                statusCodeCount.put("5xx", statusCodeCount.getOrDefault("5xx", 0) + 1);
            }

            if (i + 1 == (int) Math.ceil(0.25 * saveTestResults.size()) - 1) {
                tpsPercentiles.put("p25", calculateTPS(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
                mttfbPercentiles.put("p25", calculateAvgResponseTime(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
            } else if (i + 1 == (int) Math.ceil(0.50 * saveTestResults.size()) - 1) {
                tpsPercentiles.put("p50", calculateTPS(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
                mttfbPercentiles.put("p50", calculateAvgResponseTime(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
            } else if (i + 1 == (int) Math.ceil(0.75 * saveTestResults.size()) - 1) {
                tpsPercentiles.put("p75", calculateTPS(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
                mttfbPercentiles.put("p75", calculateAvgResponseTime(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
            } else if (i + 1 == saveTestResults.size()) {
                tpsPercentiles.put("p100", calculateTPS(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
                mttfbPercentiles.put("p100", calculateAvgResponseTime(curAvgStartAt, saveTestResults.get(i).getFinishAt(), totalRequest));
            }
        }

        // 최종 결과 업데이트
        testResult.resultUpdate(totalRequest, totalSuccess, totalError, tpsPercentiles.get("p100"), mttfbPercentiles.get("p100"));
        testResultRepository.save(testResult);

        TestResultResponseDto responseDto = TestResultResponseDto.builder()
                .testId(testResult.getId())
                .startedAt(String.valueOf(saveTestResults.get(0).getStartAt()))
                .finishedAt(String.valueOf(saveTestResults.get(0).getFinishAt()))
                .url(testTemplate.getUrl())
                .method(testTemplate.getMethod())
                .totalRequest(totalRequest)
                .totalSuccess(totalSuccess)
                .statusCodeCount(statusCodeCount)
                .totalError(totalError)
                .totalUsers(testTemplate.getVuser())
                .mttfbPercentiles(mttfbPercentiles)
                .tpsPercentiles(tpsPercentiles)
                .build();

        return responseDto;
    }

    private Mono<TempSaveTestResultDto> createAndProcessRequest(TestTemplate testTemplate, TestResult TestResult) {

        long startTime = System.currentTimeMillis();
        Mono<ResponseEntity<String>> resultMono = createRequest(webClient, testTemplate);

        return resultMono.publishOn(Schedulers.boundedElastic()).flatMap(response -> {
            long endTime = System.currentTimeMillis();
            requestCounter.incrementTotalRequests();

            HttpStatusCode statusCode = response.getStatusCode();

            boolean isSuccess = false;
            boolean isError = false;
            if (statusCode.is2xxSuccessful()) {
                isSuccess = true;
            } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                isError = true;
            }

            int totalRequests = requestCounter.getTotalRequests();

            double tpsAvgTime = calculateTPS(startTime, endTime, totalRequests);
            double avgResponseTime = calculateAvgResponseTime(startTime, endTime, totalRequests);

            TempSaveTestResultDto tempSaveTestResultDto = TempSaveTestResultDto.builder()
                    .startAt(startTime)
                    .finishAt(endTime)
                    .success(isSuccess ? 1 : 0)
                    .error(isError ? 1 : 0)
                    .statusCode(statusCode.value())
                    .tpsAvg(tpsAvgTime)
                    .mttbfbAvg(avgResponseTime)
                    .build();

            saveTps(TestResult, startTime, tpsAvgTime, endTime);
            saveMttfb(TestResult, endTime, startTime, avgResponseTime);

            return Mono.just(tempSaveTestResultDto);

        }).onErrorResume(throwable -> {
            log.error("Error occurred: " + throwable.getMessage());

            requestCounter.incrementTotalRequests();
            requestCounter.incrementTotalErrors();

            saveErrorLog(throwable);
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        });
    }

    private void saveErrorLog(Throwable throwable) {
        TestErrorLog testErrorLog = TestErrorLog.builder()
                .message(throwable.getMessage())
                .build();

        testErrorLogRepository.save(testErrorLog);
    }

    private void saveMttfb(TestResult TestResult, long endTime, long startTime, double avgResponseTime) {
        long networkDelay = (long) (endTime - startTime - avgResponseTime);
        double mttfb = avgResponseTime - networkDelay;

        TestMttfb testMttfb = TestMttfb.builder()
                .testResult(TestResult)
                .mttfb(mttfb)
                .startAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
                .finishAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()))
                .build();
        testMttfbRepository.save(testMttfb);
    }

    private void saveTps(TestResult TestResult, long startTime, double tpsAvgTime, long endTime) {
        TestTps testTps = TestTps.builder()
                .testResult(TestResult)
                .startAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
                .transaction(tpsAvgTime)
                .finishAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()))
                .build();

        testTpsRepository.save(testTps);
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
