package org.benchmarker.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.model.BaseTime;


@Slf4j
@Setter
@Getter
@Entity
@Table(name = "APP_USER")
@Builder
@ToString
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private UserGroup userGroup;

}
