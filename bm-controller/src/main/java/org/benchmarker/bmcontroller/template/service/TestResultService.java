package org.benchmarker.bmcontroller.template.service;

import static org.benchmarker.bmcontroller.template.common.TemplateUtils.addPercentile;
import static org.benchmarker.bmcontroller.template.common.TemplateUtils.calculateAvgResponseTime;
import static org.benchmarker.bmcontroller.template.common.TemplateUtils.calculateTPS;
import static org.benchmarker.bmcontroller.template.common.TemplateUtils.createRequest;
import static org.benchmarker.bmcontroller.template.common.TemplateUtils.getStatusCodeCategory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.beans.RequestCounter;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcommon.dto.TempSaveTestResultDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.model.TemplateResultErrorLog;
import org.benchmarker.bmcontroller.template.model.Mttfb;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.model.Tps;
import org.benchmarker.bmcontroller.template.repository.TemplateResultErrorLogRepository;
import org.benchmarker.bmcontroller.template.repository.MttfbRepository;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.repository.TpsRepository;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultService extends AbstractTestResultService {

    private final TestTemplateRepository testTemplateRepository;

    private final TestResultRepository testResultRepository;

    private final TpsRepository tpsRepository;

    private final MttfbRepository mttfbRepository;

    private final TemplateResultErrorLogRepository templateResultErrorLogRepository;

    private final UserGroupRepository userGroupRepository;

    private final WebClient webClient;

    private final RequestCounter requestCounter;

    @Override
    public TestResultResponseDto measurePerformance(String group_id, Integer templateId, String action) throws InterruptedException {

        // 존재하는 그룹인지 파악
        UserGroup userGroup = userGroupRepository.findById(group_id)
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));

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

        long currentStartTime = saveTestResults.get(0).getStartAt();

        for (int i = 0; i < saveTestResults.size(); i++) {
            TempSaveTestResultDto tempDto = saveTestResults.get(i);
            totalSuccess += tempDto.getSuccess();
            totalError += tempDto.getError();

            int statusCode = tempDto.getStatusCode();
            String statusCodeCategory = getStatusCodeCategory(statusCode);
            statusCodeCount.put(statusCodeCategory, statusCodeCount.getOrDefault(statusCodeCategory, 0) + 1);

            int index = i + 1;
            if (index == (int) Math.ceil(0.25 * totalRequest)) {
                addPercentile(tpsPercentiles, mttfbPercentiles, "p25", currentStartTime, tempDto.getFinishAt(), totalRequest);
            } else if (index == (int) Math.ceil(0.50 * totalRequest)) {
                addPercentile(tpsPercentiles, mttfbPercentiles, "p50", currentStartTime, tempDto.getFinishAt(), totalRequest);
            } else if (index == (int) Math.ceil(0.75 * totalRequest)) {
                addPercentile(tpsPercentiles, mttfbPercentiles, "p75", currentStartTime, tempDto.getFinishAt(), totalRequest);
            } else if (index == totalRequest) {
                addPercentile(tpsPercentiles, mttfbPercentiles, "p100", currentStartTime, tempDto.getFinishAt(), totalRequest);
            }
        }

        // 최종 결과 업데이트
        testResult.resultUpdate(totalRequest, totalSuccess, totalError, tpsPercentiles.get("p100"), mttfbPercentiles.get("p100"));
        testResultRepository.save(testResult);

        return TestResultResponseDto.builder()
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
    }

    private Mono<TempSaveTestResultDto> createAndProcessRequest(TestTemplate testTemplate, TestResult TestResult) {

        long startTime = System.currentTimeMillis();
        Mono<ResponseEntity<String>> resultMono = createRequest(webClient, testTemplate).log();

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

            long endTime = System.currentTimeMillis();
            saveErrorLog(throwable);

            TempSaveTestResultDto defaultValue = TempSaveTestResultDto.builder()
                    .startAt(startTime)
                    .finishAt(endTime)
                    .success(0)
                    .error(1)
                    .statusCode(ErrorCode.BAD_REQUEST.getHttpStatus())
                    .tpsAvg(0.0)
                    .mttbfbAvg(0.0)
                    .build();

            return Mono.just(defaultValue);
        }).doOnCancel(() -> {
            // 비동기 작업이 중단될 때 호출됨
            log.info("Request cancelled. Stopping the processing.");
            // 여기에 비동기 작업 중지 로직 추가
            // 예를 들어, 해당 쓰레드의 실행을 중지하거나 작업을 취소하는 등의 작업을 수행할 수 있습니다.
        }).log();
    }

    private void saveErrorLog(Throwable throwable) {
        long endTime = System.currentTimeMillis();
        TemplateResultErrorLog templateResultErrorLog = TemplateResultErrorLog.builder()
                .message(throwable.getMessage())
                .build();

        templateResultErrorLogRepository.save(templateResultErrorLog);
    }

    private void saveMttfb(TestResult TestResult, long endTime, long startTime, double avgResponseTime) {
        long networkDelay = (long) (endTime - startTime - avgResponseTime);
        double mttfb = avgResponseTime - networkDelay;

        Mttfb testMttfb = Mttfb.builder()
                .testResult(TestResult)
                .mttfb(mttfb)
                .startAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
                .finishAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()))
                .build();
        mttfbRepository.save(testMttfb);
    }

    private void saveTps(TestResult TestResult, long startTime, double tpsAvgTime, long endTime) {
        Tps tps = Tps.builder()
                .testResult(TestResult)
                .startAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
                .transaction(tpsAvgTime)
                .finishAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()))
                .build();

        tpsRepository.save(tps);
    }

    @Override
    public TestTemplateResponseDto getTemplateResult(Integer templateResultId) {

        TestTemplate testTemplate = testTemplateRepository.findById(templateResultId)
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        return testTemplate.convertToResponseDto();
    }

    @Override
    public List<TestResultResponseDto> getGroupTemplateResult(String groupId) {

        // 존재하는 그룹인지 파악
        UserGroup userGroup = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));

        List<TestTemplate> testTemplates = testTemplateRepository.findAllByUserGroupId(userGroup.getId());
        List<TestResultResponseDto> results = new ArrayList<>();
        for (int i = 0; i < testTemplates.size(); i++) {
            TestResult tempResult = testResultRepository.findByTestTemplate(testTemplates.get(i));
            results.add(tempResult.convertToResponseDto());
        }

        return results;
    }
}
