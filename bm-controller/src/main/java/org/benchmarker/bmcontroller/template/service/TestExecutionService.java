package org.benchmarker.bmcontroller.template.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.preftest.common.TestInfo;
import org.benchmarker.bmcontroller.template.model.TestExecution;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.repository.TestExecutionRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
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
    public TestExecution init(TestInfo testInfo){
        TestTemplate testTemplate = testTemplateRepository.findById(testInfo.getTemplateId())
            .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        TestExecution test = TestExecution.builder()
            .id(UUID.fromString(testInfo.getTestId()))
            .testTemplate(testTemplate)
            .build();
        return testExecutionRepository.save(test);
    }

}
