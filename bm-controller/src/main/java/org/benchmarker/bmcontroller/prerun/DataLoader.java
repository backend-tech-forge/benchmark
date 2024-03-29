package org.benchmarker.bmcontroller.prerun;

import static org.benchmarker.bmcontroller.common.util.NoOp.noOp;
import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;
import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_NAME;

import jakarta.transaction.Transactional;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmcontroller.agent.AgentServerManager;
import org.benchmarker.bmcontroller.scheduler.ScheduledTaskService;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.model.enums.GroupRole;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * After the application starts, this class will be executed to add the default user to the
 * database.
 *
 * @see org.springframework.boot.CommandLineRunner
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;
    private final PasswordEncoder passwordEncoder;
    private final ScheduledTaskService scheduledTaskService;
    private final AgentServerManager agentServerManager;

    @Value("${admin.id}")
    private String adminId;
    @Value("${admin.password}")
    private String adminPassword;
    @Autowired
    private DiscoveryClient discoveryClient;

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

        // remove & add agent in every seconds
        scheduledTaskService.start(-100L, () -> {
            // agent health check
            Iterator<Entry<String, AgentInfo>> iterator = agentServerManager.getAgentsUrl()
                .entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, AgentInfo> next = iterator.next();
                try {
                    ResponseEntity<AgentInfo> agentInfo = WebClient.create(next.getKey())
                        .get()
                        .uri("/api/status")
                        .retrieve()
                        .toEntity(AgentInfo.class)
                        .block();

                    assert agentInfo != null;
                    if (agentInfo.getStatusCode().is2xxSuccessful()) {
                        next.setValue(Objects.requireNonNull(agentInfo.getBody()));
                    } else {
                        iterator.remove();
                    }
                } catch (Exception e) {
                    iterator.remove();
                }
            }

            // get current agent from eureka discovery
            List<ServiceInstance> instances = discoveryClient.getInstances("bm-agent");
            // 각 인스턴스의 URL을 사용하여 요청을 보냄
            for (ServiceInstance instance : instances) {
                try{
                    String serverUrl = instance.getUri().toString();
                    AgentInfo agentInfo = WebClient.create(serverUrl)
                        .get()
                        .uri("/api/status")
                        .retrieve()
                        .bodyToMono(AgentInfo.class)
                        .block();

                    agentInfo.setServerUrl(serverUrl);
                    agentServerManager.add(serverUrl, agentInfo);
                }catch (Exception e){
                    noOp();
                }
            }
            messagingTemplate.convertAndSend("/topic/server",
                agentServerManager.getAgentsUrl().values());
        }, 0, 500, TimeUnit.MILLISECONDS);


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