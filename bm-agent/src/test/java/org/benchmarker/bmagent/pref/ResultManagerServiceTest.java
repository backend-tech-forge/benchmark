package org.benchmarker.bmagent.pref;

import static org.assertj.core.api.Assertions.assertThat;

import org.benchmarker.bmcommon.dto.CommonTestResult;
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
        CommonTestResult commonTestResult = RandomUtils.generateRandomTestResult();
        resultManagerService.save(1L, commonTestResult);

        // when
        CommonTestResult result = resultManagerService.find(1L);

        // then
        assertThat(result).isEqualTo(commonTestResult);
    }

    @Test
    @DisplayName("TestResult 저장 및 삭제 성공")
    void saveAndRemove() {
        // given
        CommonTestResult commonTestResult = RandomUtils.generateRandomTestResult();
        resultManagerService.save(1L, commonTestResult);

        // when
        resultManagerService.remove(1L);

        CommonTestResult findResult = resultManagerService.find(1L);

        // then
        assertThat(findResult).isNull();
    }
}
