package org.benchmarker.pre;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.benchmarker.user.model.Role;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.benchmarker.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;
import static org.benchmarker.user.constant.UserConsts.USER_GROUP_DEFAULT_NAME;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.id}")
    private String adminId;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 데이터베이스에 초기 사용자 추가
        userGroupRepository.save(defaultUserGroup());
        userRepository.save(adminUser());
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
                .userGroup(defaultUserGroup())
                .role(Role.ROLE_ADMIN)
                .build();
    }
}