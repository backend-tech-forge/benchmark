package org.benchmarker.bmcontroller.mail.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResDto {

    private String mail;

    private String certificationCode;
}
