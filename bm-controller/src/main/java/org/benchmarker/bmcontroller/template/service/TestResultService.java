package org.benchmarker.bmcontroller.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.model.TestMttfb;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.model.TestTps;
import org.benchmarker.bmcontroller.template.repository.TestMttfbRepository;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.repository.TestTpsRepository;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultService extends AbstractTestResultService {

    private final TestTemplateRepository testTemplateRepository;

    private final TestResultRepository testResultRepository;

    private final TestTpsRepository testTpsRepository;

    private final TestMttfbRepository testMttfbRepository;

    private final UserGroupRepository userGroupRepository;

    @Override
    public Optional<CommonTestResult> resultSaveAndReturn(CommonTestResult commonTestResult) {

        // 템플릿이 존재하는지 먼저 파악.
        TestTemplate testTemplate = testTemplateRepository.findById(commonTestResult.getTestId())
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startedAt = LocalDateTime.parse(commonTestResult.getStartedAt(), formatter);
        LocalDateTime finishedAt = LocalDateTime.parse(commonTestResult.getFinishedAt(), formatter);

        TestResult testResult = TestResult.builder()
                .testTemplate(testTemplate)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .totalRequest(commonTestResult.getTotalRequests())
                .totalSuccess(commonTestResult.getTotalSuccess())
                .totalError(commonTestResult.getTotalErrors())
                .tpsAvg(commonTestResult.getTpsAverage())
                .mttbfbAvg(commonTestResult.getMttfbAverage())
                .build();

        TestResult saveTestResult = testResultRepository.save(testResult);

        saveTps(saveTestResult, startedAt, finishedAt, commonTestResult.getTpsAverage());
        saveMttfb(saveTestResult, startedAt, finishedAt, commonTestResult.getMttfbAverage());

        return Optional.of(commonTestResult);
    }

    private void saveMttfb(TestResult TestResult, LocalDateTime startTime, LocalDateTime endTime, String avgResponseTime) {
        TestMttfb testMttfb = TestMttfb.builder()
                .testResult(TestResult)
                .startAt(startTime)
                .finishAt(endTime)
                .mttfb(avgResponseTime)
                .build();

        testMttfbRepository.save(testMttfb);
    }

    private void saveTps(TestResult TestResult, LocalDateTime startTime, LocalDateTime endTime, double tpsAvgTime) {
        TestTps testTps = TestTps.builder()
                .testResult(TestResult)
                .startAt(startTime)
                .finishAt(endTime)
                .transaction(tpsAvgTime)
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
