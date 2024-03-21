package org.benchmarker.bmcontroller.user.helper;

import org.benchmarker.bmcontroller.user.controller.constant.TestUserConsts;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.enums.Role;

/**
 * Helper class for testing user
 */
public abstract class UserHelper {

    /**
     * Create default user
     *
     * @return default user
     */
    public static User createDefaultUser() {
        return User.builder()
            .id(TestUserConsts.id)
            .email(TestUserConsts.email)
            .emailNotification(TestUserConsts.emailNotification)
            .password(TestUserConsts.password)
            .slackNotification(TestUserConsts.slackNotification)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .role(TestUserConsts.role)
            .build();
    }

    public static User createDefaultAdmin() {
        return User.builder()
            .id("adminId")
            .email(TestUserConsts.email)
            .emailNotification(TestUserConsts.emailNotification)
            .password(TestUserConsts.password)
            .slackNotification(TestUserConsts.slackNotification)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .role(Role.ROLE_ADMIN)
            .build();
    }

    /**
     * Create default user with id
     *
     * @param id user id
     * @return default user
     */
    public static User createDefaultUser(String id) {
        return User.builder()
            .id(id)
            .email(TestUserConsts.email)
            .emailNotification(TestUserConsts.emailNotification)
            .password(TestUserConsts.password)
            .slackNotification(TestUserConsts.slackNotification)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .role(TestUserConsts.role)
            .build();
    }

    /**
     * Create default user group
     *
     * @return default user group
     */
    public static UserGroup createDefaultUserGroup() {
        return UserGroup.builder()
            .id(TestUserConsts.groupId)
            .name(TestUserConsts.groupName)
            .build();
    }

    /**
     * Create default user group with id
     *
     * @param groupId
     * @return default user group
     */
    public static UserGroup createDefaultUserGroup(String groupId) {
        return UserGroup.builder()
            .id(groupId)
            .name(TestUserConsts.groupName)
            .build();
    }
}
