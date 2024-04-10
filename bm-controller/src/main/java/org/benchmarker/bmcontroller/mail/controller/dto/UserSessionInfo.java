package org.benchmarker.bmcontroller.mail.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionInfo {

    private String certificationCode;

    private String userMail;

    private boolean isVerification;

    private LocalDateTime verificationTime;

    private LocalDateTime updateTime;

    public boolean verificationCode(String code) {
        return this.certificationCode.equals(code);
    }

    public void changeStatus() {
        this.isVerification = !this.isVerification;
        this.updateTime = LocalDateTime.now();
    }
}
