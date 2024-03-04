package org.benchmarker.user.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.benchmarker.user.model.User;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDto {

    @NotBlank
    @Size(min = 4, max = 20)
    private String id;
    @NotBlank
    @Size(min = 4, max = 20)
    private String password;
    @NotBlank
    private String slackWebhookUrl;
    private Boolean slackNotification;
    @Email
    private String email;
    private Boolean emailNotification;

    @Builder
    public UserRegisterDto(String id, String password, String slackWebhookUrl,
        Boolean slackNotification, String email, Boolean emailNotification) {
        this.id = id;
        this.password = password;
        this.slackWebhookUrl = slackWebhookUrl;
        this.slackNotification = slackNotification;
        this.email = email;
        this.emailNotification = emailNotification;
    }

    public User toEntity() {
        return User.builder()
            .id(id)
            .password(password)
            .slackWebhookUrl(slackWebhookUrl)
            .slackNotification(slackNotification)
            .email(email)
            .emailNotification(emailNotification)
            .build();
    }
}
