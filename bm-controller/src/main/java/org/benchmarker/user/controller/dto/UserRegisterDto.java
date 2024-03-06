package org.benchmarker.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;

@Getter
@Setter
@Builder
@AllArgsConstructor
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

    @JsonProperty(required = false)
    private UserGroup userGroup;


    public User toEntity() {
        User user = User.builder()
            .id(id)
            .password(password)
            .slackWebhookUrl(slackWebhookUrl)
            .slackNotification(slackNotification)
            .email(email)
            .emailNotification(emailNotification)
            .build();
        if (userGroup != null) {
            user.setUserGroup(userGroup);
        }
        return user;
    }
}
