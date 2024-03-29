package org.benchmarker.user.controller.constant;

import org.benchmarker.user.model.enums.Role;

public interface TestUserConsts {

    String id = "test_id";
    String password = "test_password";
    String email = "test_email@gmail.com";
    String slackWebhookUrl = "test_slack_webhook_url";
    String groupId = "test_group_id";
    String groupName = "test_group_name";
    Boolean slackNotification = false;
    Boolean emailNotification = false;
    Role role = Role.ROLE_USER;
}
