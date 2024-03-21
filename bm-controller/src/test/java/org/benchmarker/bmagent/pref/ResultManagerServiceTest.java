package org.benchmarker.bmagent.pref;

import static org.assertj.core.api.Assertions.assertThat;

import org.benchmarker.bmcommon.dto.TestResult;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ResultManagerService.class})
public class ResultManagerServiceTest {
    @Autowired
    private ResultManagerService resultManagerService;

    @Test
    @DisplayName("TestResult 저장 및 조회 성공")
    void saveAndFind() {
        // given
        TestResult testResult = RandomUtils.generateRandomTestResult();
        resultManagerService.save(1L, testResult);

        // when
        TestResult result = resultManagerService.find(1L);

        // then
        assertThat(result).isEqualTo(testResult);
    }

    @Test
    @DisplayName("TestResult 저장 및 삭제 성공")
    void saveAndRemove() {
        // given
        TestResult testResult = RandomUtils.generateRandomTestResult();
        resultManagerService.save(1L, testResult);

        // when
        resultManagerService.remove(1L);

        TestResult findResult = resultManagerService.find(1L);

        // then
        assertThat(findResult).isNull();
    }
}
