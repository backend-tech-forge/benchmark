package org.benchmarker.bmcontroller.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegisterDto {

    @NotBlank
    @Size(min = 4, max = 20)
    private String id;
    @NotBlank
    @Size(min = 4, max = 20)
    private String password;
    @NotBlank
    @JsonProperty("slack_webhook_url")
    private String slackWebhookUrl;
    @JsonProperty("slack_notification")
    private Boolean slackNotification;
    @Email
    private String email;
    @JsonProperty("email_notification")
    private Boolean emailNotification;

    @JsonProperty("groups")
    private List<UserGroup> userGroup;


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
