package org.benchmarker.bmcontroller.mail.service;

import jakarta.transaction.Transactional;
import org.benchmarker.bmcontroller.mail.common.factory.EmailBodyGenerator;
import org.benchmarker.bmcontroller.mail.common.factory.EmailVerificationFactory;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailCertificationDto;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailResDto;
import org.benchmarker.bmcontroller.mail.service.impl.MailSenderImpl;
import org.benchmarker.bmcontroller.mail.common.strategy.IRandomCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class MailSenderImplTest {

    @Mock
    private IRandomCodeGenerator randomCodeGenerator;

    @Mock
    private MailSenderImpl mailSender;

    @Test
    @DisplayName("메일 보내는 테스트")
    public void sendMailTest() {
        // Given
        EmailCertificationDto emailCertificationDto = EmailCertificationDto.builder().email("apple4rhk@naver.com").build();

        String authNum = "123456"; // 예상되는 인증 번호
        when(randomCodeGenerator.generateVerificationCode()).thenReturn(authNum);

        EmailResDto res = EmailResDto.builder()
                .mail(emailCertificationDto.getEmail())
                .certificationCode(authNum)
                .build();

        EmailBodyGenerator emailBodyGenerator = new EmailVerificationFactory();

        when(mailSender.sendMail(any(), any())).thenReturn(res);

        // When
        EmailResDto emailResDto = mailSender.sendMail(emailCertificationDto, emailBodyGenerator);

        // Then
        assertThat(emailResDto.getMail()).isEqualTo(emailCertificationDto.getEmail());
        assertThat(emailResDto.getCertificationCode()).isEqualTo(authNum);
    }

}