package org.benchmarker.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.model.BaseTime;
import java.util.List;

@Slf4j
@Setter
@Getter
@Entity
@Table(name = "APP_USER")
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User extends BaseTime {

    @Id
    private String id;
    private String password;
    @Column(name = "slack_webhook_url")
    @Builder.Default
    private String slackWebhookUrl = "";
    @Column(name = "slack_notification")
    @Builder.Default
    private Boolean slackNotification = false;
    @Column(name = "email")
    @Builder.Default
    private String email = "";
    @Column(name = "email_notification")
    @Builder.Default
    private Boolean emailNotification = false;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserGroupJoin> userGroupJoin;
}
