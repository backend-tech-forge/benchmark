package org.benchmarker.bmcontroller.template.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcontroller.preftest.helper.IntegrationTestHelper;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CommonDtoConversionTest {

    @Autowired
    private IntegrationTestHelper integrationTestHelper;
    @Autowired
    private TestResultRepository testResultRepository;


    @org.junit.jupiter.api.Test
    @Transactional
    @DisplayName("[통합 테스트] TestResult를 CommonTestResult로 변환 성공")
    void testConvertToCommonTestResult() {
        // given
        integrationTestHelper.saveBasics();
        List<TestResult> all = testResultRepository.findAll();
        assertThat(all).isNotEmpty();
        TestResult testResult = all.get(0);

        // when
        CommonTestResult result = CommonDtoConversion.convertToCommonTestResult(testResult);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStartedAt()).isEqualTo(testResult.getStartedAt().toString());
        assertThat(result.getFinishedAt()).isEqualTo(testResult.getFinishedAt().toString());
        assertThat(result.getTotalSuccess()).isEqualTo(testResult.getTotalSuccess());
        assertThat(result.getTpsAverage()).isEqualTo(testResult.getTpsAvg());
        assertThat(result.getMttfbAverage()).isEqualTo(testResult.getMttbfbAvg());
        assertThat(result.getGroupId()).isEqualTo(testResult.getTestExecution().getTestTemplate().getUserGroup().getId());

    }
}