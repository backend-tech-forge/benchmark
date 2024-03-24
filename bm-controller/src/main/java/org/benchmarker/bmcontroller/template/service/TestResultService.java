package org.benchmarker.bmcontroller.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultReqDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.model.*;
import org.benchmarker.bmcontroller.template.repository.*;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
    public Optional<SaveResultResDto> resultSaveAndReturn(SaveResultReqDto request) {

        /**
         * agent 받은 결과를 db 에 저장
         */
        // 템플릿이 존재하는지 먼저 파악.
        TestTemplate testTemplate = testTemplateRepository.findById(request.getTestId())
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        LocalDateTime startedAt = request.getStartedAt();
        LocalDateTime finishedAt = request.getFinishedAt();

        long choStartAt = startedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        long choFinishAt = finishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();

        TestResult testResult = TestResult.builder()
                .testTemplate(testTemplate)
                .startedAt(request.getStartedAt())
                .finishedAt(request.getFinishedAt())
                .totalRequest(request.getTotalRequest())
                .totalSuccess(request.getTotalSuccess())
                .totalError(request.getTotalError())
                .tpsAvg(request.getTpsAvg())
                .mttbfbAvg(request.getMttbfbAvg())
                .build();

        TestResult saveTestResult = testResultRepository.save(testResult);

        double tpsAvgTime = calculateTPS(choStartAt, choFinishAt, request.getTotalRequest());
        double avgResponseTime = calculateAvgResponseTime(choStartAt, choFinishAt, request.getTotalRequest());

        saveTps(saveTestResult, startedAt, finishedAt, tpsAvgTime);
        saveMttfb(saveTestResult, startedAt, finishedAt, avgResponseTime);
        saveTemplateResultStatus(saveTestResult, request.getStatusCode(), testTemplate.getMethod());


        return Optional.of(testResult.convertToSaveResDto());
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

    private void saveTemplateResultStatus(TestResult TestResult, String statusCode, String httpMethod) {
        TemplateResultStatus templateResultStatus = TemplateResultStatus.builder()
                .testResult(TestResult)
                .httpMethod(HttpMethod.valueOf(httpMethod.toUpperCase()))
                .resCode(statusCode)
                .build();

        templateResultStatusRepository.save(templateResultStatus);
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
