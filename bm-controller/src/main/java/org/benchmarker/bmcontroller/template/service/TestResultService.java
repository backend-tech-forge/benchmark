package org.benchmarker.bmcontroller.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.template.common.TemplateUtils;
import org.benchmarker.bmcontroller.template.controller.dto.ResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;
import org.benchmarker.bmcontroller.template.model.*;
import org.benchmarker.bmcontroller.template.repository.*;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.benchmarker.bmcontroller.template.common.TemplateUtils.calculateAvgResponseTime;
import static org.benchmarker.bmcontroller.template.common.TemplateUtils.calculateTPS;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultService extends AbstractTestResultService {

    private final TestTemplateRepository testTemplateRepository;

    private final TestResultRepository testResultRepository;

    private final TpsRepository tpsRepository;

    private final MttfbRepository mttfbRepository;

    private final TemplateResultStatusRepository templateResultStatusRepository;

    private final UserGroupRepository userGroupRepository;

    @Override
    public Optional<SaveResultResDto> resultSaveAndReturn(CommonTestResult request) {

        /**
         * agent 받은 결과를 db 에 저장
         */
        // 템플릿이 존재하는지 먼저 파악.
        TestTemplate testTemplate = testTemplateRepository.findById(request.getTestId())
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        LocalDateTime dateStartedAt = LocalDateTime.parse(request.getStartedAt());
        LocalDateTime dateFinishedAt = LocalDateTime.parse(request.getFinishedAt());

        long choStartAt = dateStartedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        long choFinishAt = dateFinishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();

        TestResult testResult = TestResult.builder()
                .testTemplate(testTemplate)
                .startedAt(dateStartedAt)
                .finishedAt(dateFinishedAt)
                .totalRequest(request.getTotalRequests())
                .totalSuccess(request.getTotalSuccess())
                .totalError(request.getTotalErrors())
                .tpsAvg(request.getTpsAverage())
                .mttbfbAvg(request.getMttfbAverage())
                .build();

        TestResult saveTestResult = testResultRepository.save(testResult);

        double tpsAvgTime = calculateTPS(choStartAt, choFinishAt, request.getTotalRequests());
        double avgResponseTime = calculateAvgResponseTime(choStartAt, choFinishAt, request.getTotalRequests());

        saveTps(saveTestResult, dateStartedAt, dateFinishedAt, tpsAvgTime);
        saveMttfb(saveTestResult, dateStartedAt, dateFinishedAt, avgResponseTime);
        saveTemplateResultStatus(saveTestResult, request.getStatusCode(), testTemplate.getMethod());

        return Optional.of(saveTestResult.convertToSaveResDto());
    }

    private void saveMttfb(TestResult TestResult, LocalDateTime startTime, LocalDateTime finishTime, double avgResponseTime) {
        Mttfb testMttfb = Mttfb.builder()
                .testResult(TestResult)
                .mttfbAvg(avgResponseTime)
                .startAt(startTime)
                .finishAt(finishTime)
                .build();

        mttfbRepository.save(testMttfb);
    }

    private void saveTps(TestResult TestResult, LocalDateTime startTime, LocalDateTime finishTime, double tpsAvgTime) {
        Tps tps = Tps.builder()
                .testResult(TestResult)
                .startAt(startTime)
                .finishAt(finishTime)
                .transaction(tpsAvgTime)
                .build();

        tpsRepository.save(tps);
    }

    private void saveTemplateResultStatus(TestResult TestResult, int statusCode, String httpMethod) {
        TemplateResultStatus templateResultStatus = TemplateResultStatus.builder()
                .testResult(TestResult)
                .httpMethod(HttpMethod.valueOf(httpMethod.toUpperCase()))
                .resCode(statusCode)
                .build();

        templateResultStatusRepository.save(templateResultStatus);
    }

    @Override
    public ResultResDto getTemplateResult(Integer templateResultId) {

        TestTemplate testTemplate = testTemplateRepository.findById(templateResultId)
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        List<TestResult> testResults = testResultRepository.findByTestTemplate(testTemplate);

        // 시간순으로 정렬
        int totalRequest = testResults.size();
        int totalSuccess = 0;
        int totalError = 0;

        Map<String, Integer> statusCodeCount = new HashMap<>();
        Map<String, Double> mttfbPercentiles = new HashMap<>();
        Map<String, Double> tpsPercentiles = new HashMap<>();

        for (int i = 0; i < testResults.size(); i++) {
            TestResult tempDto = testResults.get(i);

            totalSuccess += tempDto.getTotalSuccess();
            totalError += tempDto.getTotalError();

            List<TemplateResultStatus> statuses = templateResultStatusRepository.findByTestResult(tempDto);

            for (int j = 0; j < statuses.size(); j++) {
                int statusCode = statuses.get(j).getResCode();
                String statusCodeCategory = TemplateUtils.getStatusCodeCategory(statusCode);
                statusCodeCount.put(statusCodeCategory, statusCodeCount.getOrDefault(statusCodeCategory, 0) + 1);
            }

            long choStartAt = tempDto.getStartedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            long choFinishAt = tempDto.getFinishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

            int index = i + 1;
            if (index == (int) Math.ceil(0.25 * totalRequest)) {
                TemplateUtils.addPercentile(tpsPercentiles, mttfbPercentiles, "p25", choStartAt, choFinishAt, totalRequest);
            } else if (index == (int) Math.ceil(0.50 * totalRequest)) {
                TemplateUtils.addPercentile(tpsPercentiles, mttfbPercentiles, "p50", choStartAt, choFinishAt, totalRequest);
            } else if (index == (int) Math.ceil(0.75 * totalRequest)) {
                TemplateUtils.addPercentile(tpsPercentiles, mttfbPercentiles, "p75", choStartAt, choFinishAt, totalRequest);
            } else if (index == totalRequest) {
                TemplateUtils.addPercentile(tpsPercentiles, mttfbPercentiles, "p100", choStartAt, choFinishAt, totalRequest);
            }
        }

        long choFirstStartAt = testResults.get(0).getStartedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        long choEndFinishAt = testResults.get(testResults.size() - 1).getFinishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        return ResultResDto.builder()
                .testId(testTemplate.getId())
                .startedAt(String.valueOf(testResults.get(0).getStartedAt()))
                .finishedAt(String.valueOf(testResults.get(testResults.size() - 1).getFinishedAt()))
                .url(testTemplate.getUrl())
                .body(testTemplate.getBody())
                .method(testTemplate.getMethod())
                .totalRequest(totalRequest)
                .totalSuccess(totalSuccess)
                .totalError(totalError)
                .statusCodeCount(statusCodeCount)
                .mttfbPercentiles(mttfbPercentiles)
                .tpsPercentiles(tpsPercentiles)
                .totalUsers(totalRequest)
                .totalDuration("")
                .tpsAvg(calculateTPS(choFirstStartAt, choEndFinishAt, totalRequest))
                .mttbfbAvg(calculateAvgResponseTime(choFirstStartAt, choEndFinishAt, totalRequest))
                .build();
    }
}
