package org.benchmarker.bmcontroller.prerun;

import static org.benchmarker.bmcontroller.common.util.NoOp.noOp;
import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;
import static org.benchmarker.bmcontroller.user.constant.UserConsts.USER_GROUP_DEFAULT_NAME;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.ArrayList;
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
 * @see CommandLineRunner
 */
@Component
@Slf4j
@Profile("kubernetes")
@RequiredArgsConstructor
public class DataLoaderKubernetes implements CommandLineRunner {

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

            List<String> podNames = new ArrayList<>();

            try {
                // Kubernetes 클라이언트 설정
                ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);

                // CoreV1Api 객체 생성
                CoreV1Api api = new CoreV1Api();

                V1PodList podList = api.listNamespacedPod("default", null, null, null, null,
                    "app=agent-service", null, null, null, null, null, null);
                // Pod 목록 순회
                for (V1Pod pod : podList.getItems()) {
                    // Pod 이름 출력
                    String podIP = pod.getStatus().getPodIP();
                    podNames.add("http://" + podIP + ":8081");
                }

            } catch (ApiException e) {
                System.err.println(
                    "Exception when calling CoreV1Api#listServiceForAllNamespaces");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }

//            podNames = discoveryClient.getInstances("bm-agent").stream()
//                .map((serviceInstance -> {
//                    return serviceInstance.getUri().toString();
//                })).toList();

            Flux.fromIterable(podNames)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(instanceUrl -> {
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
                            agentInfo.setServerUrl(instanceUrl); // 수정된 부분
                            return agentInfo;
                        });
                })
                .sequential()
                .doOnNext(agentInfo -> {
                    if (agentInfo != null) {
                        log.info("agentInfo {}", agentInfo.toString());
                        agentServerManager.add(agentInfo.getServerUrl(), agentInfo);
                    }
                })
                .blockLast();

            messagingTemplate.convertAndSend("/topic/server",
                agentServerManager.getAgentsUrl().values());

        }, 0, 1000, TimeUnit.MILLISECONDS);


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