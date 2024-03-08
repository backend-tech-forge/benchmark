package org.benchmarker.user.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
    @JsonProperty("groups")
    private List<UserGroup> userGroup;

    @Builder
    public UserInfo(String id, String slackWebhookUrl, Boolean slackNotification, String email,
        Boolean emailNotification, List<UserGroup> userGroup) {
        this.id = id;
        this.slackWebhookUrl = slackWebhookUrl;
        this.slackNotification = slackNotification;
        this.email = email;
        this.emailNotification = emailNotification;
        this.userGroup = userGroup;
    }


    /**
     * Convert User to UserInfo
     *
     * Need to run in the same transaction
     *
     * @param user
     * @return
     */
    public static UserInfo from(User user) {
        return UserInfo.builder()
            .id(user.getId())
            .slackWebhookUrl(user.getSlackWebhookUrl())
            .slackNotification(user.getSlackNotification())
            .email(user.getEmail())
            .emailNotification(user.getEmailNotification())
            .userGroup(user.getUserGroupJoin().stream().map(UserGroupJoin::getUserGroup).toList())
            .build();
    }
}
