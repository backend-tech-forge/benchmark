package org.benchmark.bmcontroller.init;

import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.benchmarker.user.service.UserService;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
public class InitiClass {
    @SpyBean
    protected UserService userService;
    @SpyBean
    protected UserRepository userRepository;
    @SpyBean
    protected UserGroupRepository userGroupRepository;

    @AfterEach
    void removeAll() {
        userRepository.deleteAll();
        userGroupRepository.deleteAll();
    }
}
