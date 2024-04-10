package org.benchmarker.bmcontroller.mail.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailCertificationDto;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailResDto;
import org.benchmarker.bmcontroller.mail.service.IMailSender;
import org.benchmarker.bmcontroller.mail.strategy.IRandomCodeGenerator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderImpl implements IMailSender {

    private final JavaMailSender javaMailSender;

    private final IRandomCodeGenerator randomNumber;

    @Override
    public EmailResDto sendMail(EmailCertificationDto emailCertificationDto) {
        String authNum = randomNumber.generateVerificationCode();

        String subject = "회원 가입 인증 코드";
        String body = "안녕하세요!\n\n회원 가입을 위한 인증 코드를 안내드립니다. 아래의 인증 코드를 입력하여 계정을 활성화하세요:\n\n"
                + "인증 코드: " + authNum;

        log.info("Start mail sender!!");
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailCertificationDto.getEmail()); // 메일 수신자
            mimeMessageHelper.setSubject(subject); // 메일 제목
            mimeMessageHelper.setText(body, false); // 메일 본문 내용, HTML 여부

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.info("Mail sender fail!!");
            throw new RuntimeException(e);
        }

        return EmailResDto.builder()
                .mail(emailCertificationDto.getEmail())
                .certificationCode(authNum)
                .build();
    }

}
