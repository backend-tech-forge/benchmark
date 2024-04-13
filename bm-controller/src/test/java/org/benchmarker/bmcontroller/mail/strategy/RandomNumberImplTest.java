package org.benchmarker.bmcontroller.mail.strategy;

import org.benchmarker.bmcontroller.mail.common.strategy.IRandomCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomNumberImplTest {

    @Test
    @DisplayName("6자리 난수 생성 하는지 확인 테스트")
    public void randomNumberCreate() {
        // given
        IRandomCodeGenerator randomNumber = () -> "123456";

        // when
        String code = randomNumber.generateVerificationCode();

        // then
        assertThat(code).isEqualTo("123456");
    }

}