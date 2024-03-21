package org.benchmarker.bmcontroller.prerun;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmcontroller.user.model.enums.GroupRole;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.benchmarker.bmcontroller.common.util.NoOp.noOp;
import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;
import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_NAME;

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
            () -> userGroupRepository.save(defaultAdminGroup()));
        List<UserGroupJoin> findJoin = userGroupJoinRepository.findByUserId(adminId);
        if (findJoin.isEmpty()) {
            Optional<UserGroup> group = userGroupRepository.findById(USER_GROUP_DEFAULT_ID);
            group.ifPresent(userGroup -> userGroupJoinRepository.save(
                UserGroupJoin.builder()
                    .user(adminUser())
                    .userGroup(defaultAdminGroup())
                    .role(GroupRole.LEADER)
                    .build()
            ));
        }
    }

    private UserGroup defaultAdminGroup() {
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