package org.benchmarker.bmcontroller.login.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestInfo {

    private String id;
    private String password;

    @Builder
    public LoginRequestInfo(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public LoginRequestInfo() {
    }
}
