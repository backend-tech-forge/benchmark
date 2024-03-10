package org.benchmarker.prerun;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.benchmarker.user.model.enums.Role;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;
import org.benchmarker.user.repository.UserGroupJoinRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.benchmarker.common.util.NoOp.noOp;
import static org.benchmarker.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;
import static org.benchmarker.user.constant.UserConsts.USER_GROUP_DEFAULT_NAME;

/**
 * After the application starts, this class will be executed to add the default user to the
 * database.
 *
 * @see org.springframework.boot.CommandLineRunner
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.id}")
    private String adminId;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 데이터베이스에 초기 사용자 추가
        Optional<User> adminUser = userRepository.findById(adminId);
        Optional<UserGroup> defaultGroup = userGroupRepository.findById(USER_GROUP_DEFAULT_ID);
        adminUser.ifPresentOrElse(
            user -> {
                noOp();
            },
            () -> userRepository.save(adminUser()));
        defaultGroup.ifPresentOrElse(
            userGroup -> {
                noOp();
            },
            () -> userGroupRepository.save(defaultUserGroup()));
        List<UserGroupJoin> findJoin = userGroupJoinRepository.findByUserId(adminId);
        if (findJoin.isEmpty()) {
            Optional<UserGroup> group = userGroupRepository.findById(USER_GROUP_DEFAULT_ID);
            group.ifPresent(userGroup -> userGroupJoinRepository.save(
                UserGroupJoin.builder()
                    .user(adminUser())
                    .userGroup(defaultUserGroup())
                    .build()
            ));
        }
    }

    private UserGroup defaultUserGroup() {
        return UserGroup.builder()
            .id(USER_GROUP_DEFAULT_ID)
            .name(USER_GROUP_DEFAULT_NAME)
            .build();
    }

    private User adminUser() {
        return User.builder()
            .id(adminId)
            .password(passwordEncoder.encode(adminPassword))
            .email("admin@gmail.com")
            .emailNotification(false)
            .slackNotification(false)
            .slackWebhookUrl("admin-webhook-url")
            .role(Role.ROLE_ADMIN)
            .build();
    }
}