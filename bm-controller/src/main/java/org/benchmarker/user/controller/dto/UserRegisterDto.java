package org.benchmarker.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import java.util.List;

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
