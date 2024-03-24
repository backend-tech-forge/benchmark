package org.benchmarker.bmcontroller.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(parallel = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest {

    @SpyBean
    private UserRepository userRepository;
    @SpyBean
    private UserGroupRepository userGroupRepository;
    @SpyBean
    private UserGroupJoinRepository userGroupJoinRepository;

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

        // when
        UserGroup group = userGroupRepository.save(userGroup);
        User createdUser = userRepository.save(user);
        userGroupJoinRepository.save(UserGroupJoin.builder()
            .user(createdUser)
            .userGroup(group)
            .build());

        Optional<User> findUser = userRepository.findById(user.getId());

        // then
        assertThat(findUser).isNotEmpty();

        assertThat(findUser.get().getId()).isEqualTo(user.getId());
        assertThat(findUser.get().getUserGroupJoin().get(0).getUserGroup().getId()).isEqualTo(
            userGroup.getId());
        assertThat(findUser.get().getId()).isEqualTo(user.getId());
        assertThat(findUser.get().getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("새로운 그룹과 사용자 생성 시 사용자 정보를 반환한다")
    void createUser2() {
        // given
        User user = User.builder()
            .id("test")
            .password("password")
            .build();

        // when
        User createdUser = userRepository.save(user);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("default 값으로 사용자 생성시 성공한다")
    void createUserWithDefaultValues() {
        // given
        User user = User.builder()
            .id("test")
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