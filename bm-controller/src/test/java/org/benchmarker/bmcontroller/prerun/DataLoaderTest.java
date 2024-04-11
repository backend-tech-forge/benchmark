package org.benchmarker.bmcontroller.prerun;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.benchmarker.bmcontroller.agent.AgentServerManager;
import org.benchmarker.bmcontroller.scheduler.ScheduledTaskService;
import org.benchmarker.bmcontroller.user.helper.UserHelper;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Import(ScheduledTaskService.class)
class DataLoaderTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private UserGroupJoinRepository userGroupJoinRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    @Mock
    private AgentServerManager agentServerManager;
    @Mock
    private DiscoveryClient discoveryClient;

    @InjectMocks
    private DataLoader dataLoader;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("초기 DB 설정 값 확인")
    public void testRun_InitializesDatabaseWhenAdminAndGroupDoNotExist() throws Exception {
        // Mock repository methods
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        when(userGroupRepository.findById(anyString())).thenReturn(Optional.empty());

        // Call the run method
        dataLoader.run();

        // Verify that userRepository.save and userGroupRepository.save were called
        verify(userRepository).save(any());
        verify(userGroupRepository).save(any());
    }

    @Test
    @DisplayName("만약 ADMIN 유저가 없다면 save 합니다")
    public void testAddUserAndGroupIfNotExist_AdminAndGroupDoNotExist_ShouldAdd() {
        // Mock userRepository and userGroupRepository to return empty Optional (indicating non-existence)
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());
        when(userGroupRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // Call the method
        dataLoader.addUserAndGroupIfNotExist();

        // Verify that userRepository.save and userGroupRepository.save were called
        verify(userRepository).save(any());
        verify(userGroupRepository).save(any());
    }

    @Test
    @DisplayName("ADMIN 유저가 있다면, 다시 save 하지 않습니다")
    public void testAddUserAndGroupIfNotExist_AdminAndGroupExist_ShouldNotAdd() {
        // Mock userRepository and userGroupRepository to return non-empty Optional (indicating existence)
        when(userRepository.findById(anyString())).thenReturn(
            Optional.of(UserHelper.createDefaultAdmin()));
        when(userGroupRepository.findById(anyString())).thenReturn(Optional.of(new UserGroup()));

        // Call the method
        dataLoader.addUserAndGroupIfNotExist();

        // Verify that userRepository.save and userGroupRepository.save were not called
        verify(userRepository, never()).save(any());
        verify(userGroupRepository, never()).save(any());
    }
}