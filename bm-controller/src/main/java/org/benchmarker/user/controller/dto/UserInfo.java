package org.benchmarker.user.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.benchmarker.user.model.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo {

    @JsonProperty("id")
    private String id;
    @JsonProperty("slack_webhook_url")
    private String slackWebhookUrl;
    @JsonProperty("slack_notification")
    private Boolean slackNotification;
    @JsonProperty("email")
    private String email;
    @JsonProperty("email_notification")
    private Boolean emailNotification;

    @Builder
    public UserInfo(String id, String slackWebhookUrl, Boolean slackNotification, String email,
        Boolean emailNotification) {
        this.id = id;
        this.slackWebhookUrl = slackWebhookUrl;
        this.slackNotification = slackNotification;
        this.email = email;
        this.emailNotification = emailNotification;
    }

    public static UserInfo from(User user) {
        return UserInfo.builder()
            .id(user.getId())
            .slackWebhookUrl(user.getSlackWebhookUrl())
            .slackNotification(user.getSlackNotification())
            .email(user.getEmail())
            .emailNotification(user.getEmailNotification())
            .build();
    }
}
