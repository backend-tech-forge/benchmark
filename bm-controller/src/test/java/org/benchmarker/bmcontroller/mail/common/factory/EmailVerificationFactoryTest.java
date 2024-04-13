package org.benchmarker.bmcontroller.mail.common.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationFactoryTest {

    @Test
    @DisplayName("mail 본문 생성 테스트")
    void mailBodyCreate() {
        // given
        EmailBodyGenerator emailBodyGenerator = new EmailVerificationFactory();

        String result = "안녕하세요!\n\n, 회원 가입을 위한 인증 코드를 안내드립니다. 아래의 인증 코드를 입력하여 계정을 활성화하세요:\n\n인증 코드: 123456";

        // when
        String body = emailBodyGenerator.createBody("123456");

        // then
        assertThat(body).isEqualTo(result);
    }

}