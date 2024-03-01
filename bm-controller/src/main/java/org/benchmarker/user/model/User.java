package org.benchmarker.user.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.model.BaseTime;


@Slf4j
@Setter
@Getter
@Entity
@Table(name = "APP_USER")
public class User extends BaseTime {

    @Id
    private String id;
    private String password;
    @Column(name = "slack_webhook_url")
    private String slackWebhookUrl;
    @Column(name = "slack_notification")

    private Boolean slackNotification = false;
    @Column(name = "email")
    private String email;
    @Column(name = "email_notification")

    private Boolean emailNotification = false;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private UserGroup userGroup;


    @Builder
    public User(String id, String password, String slackWebhookUrl, Boolean slackNotification, String email, Boolean emailNotification, Role role, UserGroup userGroup) {
        this.id = id;
        this.password = password;
        this.slackWebhookUrl = slackWebhookUrl;
        this.slackNotification = slackNotification;
        this.email = email;
        this.emailNotification = emailNotification;
        this.role = role;
        this.userGroup = userGroup;

        if (this.role == null) {
            this.role = Role.USER;
        }
        if (this.slackNotification == null) {
            this.slackNotification = false;
        }
        if (this.emailNotification == null) {
            this.emailNotification = false;
        }
    }


    public User() {

    }
}
