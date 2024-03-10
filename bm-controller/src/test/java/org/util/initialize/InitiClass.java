package org.util.initialize;

import org.benchmarker.user.repository.UserGroupJoinRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.benchmarker.user.service.GroupService;
import org.benchmarker.user.service.UserService;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class InitiClass {

    @SpyBean
    protected UserService userService;
    @SpyBean
    protected UserRepository userRepository;
    @SpyBean
    protected UserGroupRepository userGroupRepository;
    @SpyBean
    protected PasswordEncoder passwordEncoder;
    @SpyBean
    protected UserGroupJoinRepository userGroupJoinRepository;

    @SpyBean
    protected GroupService groupService;

    @AfterEach
    void removeAll() {
        userGroupJoinRepository.deleteAll();
        userRepository.deleteAll();
        userGroupRepository.deleteAll();
    }
}
