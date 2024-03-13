package org.benchmarker.user.controller.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.benchmarker.user.model.UserGroup;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String id;
    private String password;
    private String slackWebhookUrl;
    private Boolean slackNotification;
    private String email;
    private Boolean emailNotification;
    private List<UserGroup> userGroup;

}
