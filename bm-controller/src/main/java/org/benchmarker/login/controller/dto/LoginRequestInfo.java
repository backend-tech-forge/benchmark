package org.benchmarker.login.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginRequestInfo {

    private String id;
    private String password;
}
