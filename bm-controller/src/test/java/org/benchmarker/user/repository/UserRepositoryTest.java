package org.benchmarker.user.repository;

import org.util.initialize.InitiClass;
import org.benchmarker.user.model.Role;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends InitiClass {

    @Test
    @DisplayName("정상 사용자 생성시 사용자 정보를 반환한다")
    void createUser() {
        // given
        User user = User.builder()
            .id("test")
            .password("password")
            .build();
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        userGroupRepository.save(userGroup);
        UserGroup defaultUserGroup = userGroupRepository.findById("default").get();
        user.setUserGroup(defaultUserGroup);

        // when
        User createdUser = userRepository.save(user);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(user.getId());
        assertThat(createdUser.getUserGroup()).isEqualTo(defaultUserGroup);
    }

    @Test
    @DisplayName("사용자 그룹이 없을 때 사용자 생성시 에러를 반환한다")
    void createUserWithoutGroup() {
        // given
        User user = User.builder()
            .id("test")
            .password("password")
            .build();

        // when & then
        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    @DisplayName("default 값으로 사용자 생성시 성공한다")
    void createUserWithDefaultValues() {
        // given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        userGroupRepository.save(userGroup);

        User user = User.builder()
            .id("test")
            .userGroup(userGroup)
            .password("password")
            // default values
//                .slackWebhookUrl("")
//                .email("")
//                .emailNotification(false)
//                .slackNotification(false)
//                .role(Role.ROLE_USER)
            .build();

        // when & then
        User createdUser = userRepository.save(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getRole()).isNotNull();
        assertThat(createdUser.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(createdUser.getSlackWebhookUrl()).isEqualTo("");
        assertThat(createdUser.getEmail()).isEqualTo("");
        assertThat(createdUser.getEmailNotification()).isFalse();
        assertThat(createdUser.getSlackNotification()).isFalse();
    }

}