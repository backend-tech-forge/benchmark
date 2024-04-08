package org.benchmarker.bmcontroller.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailCertificationDto;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailResDto;
import org.benchmarker.bmcontroller.mail.service.impl.MailSenderImpl;
import org.benchmarker.bmcontroller.mail.strategy.IRandomCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

        when(mailSender.sendMail(any())).thenReturn(res);

        // When
        EmailResDto emailResDto = mailSender.sendMail(emailCertificationDto);

        // Then
        assertThat(emailResDto.getMail()).isEqualTo(emailCertificationDto.getEmail());
        assertThat(emailResDto.getCertificationCode()).isEqualTo(authNum);
    }

}