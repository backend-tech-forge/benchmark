package org.benchmarker.bmcontroller.mail.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailCertificationDto;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailResDto;
import org.benchmarker.bmcontroller.mail.service.IMailSender;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailController {

    private final IMailSender mailSender;

    @PostMapping("/mail/certification")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<EmailResDto> mailSend(HttpServletRequest request,
                                                      @Valid @RequestBody EmailCertificationDto emailCertification) {

        EmailResDto temp = mailSender.sendMail(emailCertification);

        HttpSession session = request.getSession(); // 현재 요청의 세션을 가져옴
        session.setAttribute("userCertificationCode", temp.getCertificationCode());

        return ResponseEntity.ok(temp);
    }
}
