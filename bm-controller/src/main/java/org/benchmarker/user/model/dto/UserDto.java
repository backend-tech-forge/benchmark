package org.benchmarker.user.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.benchmarker.user.model.User;

@Getter
@Builder
public class UserDto {
    private String id;
    private String password;
    private String slackWebhookUrl;
    private Boolean slackNotification;
    private String email;
    private Boolean emailNotification;

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
