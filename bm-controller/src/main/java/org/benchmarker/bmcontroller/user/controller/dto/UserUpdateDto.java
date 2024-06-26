package org.benchmarker.bmcontroller.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.benchmarker.bmcontroller.user.model.UserGroup;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserUpdateDto {

    private String id;
    private String password;
    private String slackWebhookUrl;
    private Boolean slackNotification;
    private String email;
    private Boolean emailNotification;
    @JsonIgnore
    private List<UserGroup> userGroup;

}
