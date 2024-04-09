package org.benchmarker.bmcontroller.template.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.preftest.common.TestInfo;
import org.benchmarker.bmcontroller.template.model.TestExecution;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.repository.TestExecutionRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestExecutionService {

    private final TestExecutionRepository testExecutionRepository;
    private final TestTemplateRepository testTemplateRepository;

    /**
     * 테스트 시작 전 TestExecution 을 DB 에 저장합니다.
     *
     * @param testInfo
     * @return TestExecution {@link TestExecution}
     */
    @Transactional
    public TestExecution init(TestInfo testInfo) {
        TestTemplate testTemplate = testTemplateRepository.findById(testInfo.getTemplateId())
            .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        TestExecution test = TestExecution.builder()
            .id(UUID.fromString(testInfo.getTestId()))
            .testTemplate(testTemplate)
            .build();
        return testExecutionRepository.save(test);
    }

    @Transactional(readOnly = true)
    public TestExecution getTest(String id) {
        return testExecutionRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new GlobalException(ErrorCode.TEST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<TestExecution> getTestsPageable(Pageable pageable, Integer templateId) {
        return testExecutionRepository.findAllByTestTemplateId(templateId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TestInfo> getTestInfosPageable(Pageable pageable, Integer templateId) {
        Page<TestExecution> page = testExecutionRepository.findAllByTestTemplateId(templateId,
            pageable);
        return page.map(testExecution -> {
            return TestInfo.builder().testId(testExecution.getId().toString())
                .testStatus(testExecution.getAgentStatus())
                .startedAt(testExecution.getCreatedAt())
                .templateId(templateId).build();
        });
    }

    @Transactional(readOnly = true)
    public List<TestInfo> getTestInfosList(Pageable pageable, Integer templateId) {
        return testExecutionRepository.findAllByTestTemplateId(templateId, pageable).stream()
            .map((testExecution -> {
                return TestInfo.builder().testId(testExecution.getId().toString())
                    .testStatus(testExecution.getAgentStatus())
                    .templateId(templateId).build();
            })).toList();
    }

    @Transactional
    public void updateAgentStatus(String testId, AgentStatus agentStatus) {
        if (agentStatus == null) {
            return;
        }
        TestExecution testExecution = testExecutionRepository.findById(UUID.fromString(testId))
            .orElseThrow(() -> new GlobalException(ErrorCode.TEST_NOT_FOUND));
        testExecution.setAgentStatus(agentStatus);
    }
}
