package org.benchmarker.bmcontroller.mail.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.mail.common.factory.EmailBodyGenerator;
import org.benchmarker.bmcontroller.mail.common.factory.EmailVerificationFactory;
import org.benchmarker.bmcontroller.mail.controller.dto.*;
import org.benchmarker.bmcontroller.mail.service.IMailSender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailController {

    private final IMailSender mailSender;

    @PostMapping("/mail/certification")
    public ResponseEntity<EmailResDto> mailSend(HttpServletRequest request,
                                                      @Valid @RequestBody EmailCertificationDto emailCertification) {

        EmailBodyGenerator emailBodyGenerator = new EmailVerificationFactory();
        EmailResDto mailResDto = mailSender.sendMail(emailCertification, emailBodyGenerator);

        HttpSession session = request.getSession();

        UserSessionInfo userSessionInfo = UserSessionInfo.builder()
                .certificationCode(mailResDto.getCertificationCode())
                .userMail(mailResDto.getMail())
                .isVerification(false)
                .verificationTime(LocalDateTime.now())
                .build();

        session.setAttribute("userSessionInfo", userSessionInfo);

        log.info("Session data: {}", session.getAttribute("userSessionInfo").toString());

        return ResponseEntity.ok(mailResDto);
    }

    @PostMapping("/mail/certification/code")
    public ResponseEntity<EmailCodeCertificationResultDto> certificationCode(HttpServletRequest request,
                                                                             @Valid @RequestBody EmailCodeDto emailCode) {

        HttpSession session = request.getSession();
        UserSessionInfo userSessionInfo = (UserSessionInfo) session.getAttribute("userSessionInfo");

        // 세션에 userSessionInfo가 존재하지 않을 때
        if (userSessionInfo == null) {

            EmailCodeCertificationResultDto failRes = EmailCodeCertificationResultDto.builder()
                    .status("fail")
                    .message("Session expired or invalid. Please try again.")
                    .build();

            return ResponseEntity.ok(failRes);
        } else {
            if (!userSessionInfo.verificationCode(emailCode.getCode())) {

                EmailCodeCertificationResultDto failRes = EmailCodeCertificationResultDto.builder()
                        .status("fail")
                        .message("Please enter the authentication number correctly")
                        .build();

                return ResponseEntity.ok(failRes);
            }
        }

        EmailCodeCertificationResultDto successRes = EmailCodeCertificationResultDto.builder()
                .status("success")
                .message("Authentication successful!!")
                .build();

        Objects.requireNonNull(userSessionInfo).changeStatus();
        session.setAttribute("userSessionInfo", userSessionInfo);

        return ResponseEntity.ok(successRes);
    }
}
