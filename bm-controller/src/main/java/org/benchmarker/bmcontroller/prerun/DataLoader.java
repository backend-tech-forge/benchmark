package org.benchmarker.bmcontroller.prerun;

import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;
import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_NAME;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * After the application starts, this class will be executed to add the default user to the
 * database.
 *
 * @see org.springframework.boot.CommandLineRunner
 */
@Component
@Slf4j
@Profile("production")
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
    private String adminId = "admin";
    @Value("${admin.password}")
    private String adminPassword = "admin";

    private final DiscoveryClient discoveryClient;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addUserAndGroupIfNotExist();
        performAgentHealthChecks();
    }

    void addUserAndGroupIfNotExist() {
        Optional<User> adminUser = userRepository.findById(adminId);
        if (adminUser.isEmpty()) {
            userRepository.save(createAdminUser());
        }

        Optional<UserGroup> defaultGroup = userGroupRepository.findById(USER_GROUP_DEFAULT_ID);
        if (defaultGroup.isEmpty()) {
            userGroupRepository.save(createDefaultAdminGroup());
        }

        if (userGroupJoinRepository.findByUserId(adminId).isEmpty()) {
            userGroupRepository.findById(USER_GROUP_DEFAULT_ID).ifPresent(group -> {
                userGroupJoinRepository.save(UserGroupJoin.builder()
                    .user(createAdminUser())
                    .userGroup(createDefaultAdminGroup())
                    .role(GroupRole.LEADER)
                    .build());
            });
        }
    }

    void performAgentHealthChecks() {
        scheduledTaskService.start(-100L, () -> {
            Map<String, AgentInfo> agentsUrl = agentServerManager.getAgentsUrl();

            agentsUrl.entrySet().iterator().forEachRemaining(entry -> {
                String serverUrl = entry.getKey();
                AgentInfo agentInfo = entry.getValue();

                checkAgentHealth(serverUrl, agentInfo);
            });

            List<String> podNames = new ArrayList<>();
            discoveryClient.getInstances("bm-agent").forEach(serviceInstance -> {
                podNames.add(serviceInstance.getUri().toString());
            });

            Flux.fromIterable(podNames)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::fetchAgentInfo)
                .sequential()
                .doOnNext(this::addAgentToManager)
                .blockLast();

            messagingTemplate.convertAndSend("/topic/server", agentsUrl.values());
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    void checkAgentHealth(String serverUrl, AgentInfo agentInfo) {
        try {
            ResponseEntity<AgentInfo> responseEntity = WebClient.create(serverUrl)
                .get()
                .uri("/api/status")
                .retrieve()
                .toEntity(AgentInfo.class)
                .block();

            if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                agentInfo = Objects.requireNonNull(responseEntity.getBody());
            } else {
                agentServerManager.remove(serverUrl);
            }
        } catch (Exception e) {
            agentServerManager.remove(serverUrl);
            log.error("Error occurred while fetching data from {}", serverUrl, e);
        }
    }

    private Mono<AgentInfo> fetchAgentInfo(String instanceUrl) {
        return WebClient.create(instanceUrl)
            .get()
            .uri("/api/status")
            .retrieve()
            .bodyToMono(AgentInfo.class)
            .timeout(Duration.ofSeconds(1))
            .onErrorResume(e -> {
                log.error("Error occurred while fetching data from {}", instanceUrl, e);
                return Mono.empty();
            })
            .map(agentInfo -> {
                agentInfo.setServerUrl(instanceUrl);
                return agentInfo;
            });
    }

    private void addAgentToManager(AgentInfo agentInfo) {
        if (agentInfo != null) {
            agentServerManager.add(agentInfo.getServerUrl(), agentInfo);
        }
    }

    private User createAdminUser() {
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

    private UserGroup createDefaultAdminGroup() {
        return UserGroup.builder()
            .id(USER_GROUP_DEFAULT_ID)
            .name(USER_GROUP_DEFAULT_NAME)
            .build();
    }
}