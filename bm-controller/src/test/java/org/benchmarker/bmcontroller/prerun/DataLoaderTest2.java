package org.benchmarker.bmcontroller.prerun;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmcontroller.MockServer;
import org.benchmarker.bmcontroller.agent.AgentServerManager;
import org.benchmarker.bmcontroller.scheduler.ScheduledTaskService;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class DataLoaderTest2 extends MockServer {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserGroupJoinRepository userGroupJoinRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    private AgentServerManager agentServerManager;
    @SpyBean
    private DiscoveryClient discoveryClient;

    private DataLoader dataLoader;

    @BeforeEach
    void setup() {
        dataLoader = new DataLoader(messagingTemplate, userRepository, userGroupRepository,
            userGroupJoinRepository, passwordEncoder, scheduledTaskService, agentServerManager,
            discoveryClient);
    }

    @AfterEach
    void tearDown() {
        scheduledTaskService.shutdown(-100L);
    }

    @Test
    @DisplayName("[통합테스트] 시스템 스케줄러 및 Discovery 서비스 상태 체크 테스트")
    public void initialSchedulerTest()
        throws URISyntaxException, InterruptedException {

        // when
        when(discoveryClient.getInstances("bm-agent")).thenReturn(createDummyServiceInstance());

        // stubbing mock server
        addMockResponse(AgentInfo.builder().build().random(), 10);

        dataLoader.performAgentHealthChecks();
        sleep(1500); // is this necessary? n.n

        // then
        Map<String, ScheduledExecutorService> schedulers = scheduledTaskService.getSchedulers(
            -100L);
        assertThat(schedulers).isNotNull();
        assertThat(schedulers.size()).isEqualTo(1);

        ConcurrentHashMap<String, AgentInfo> agentsUrl = agentServerManager.getAgentsUrl();
        assertThat(agentsUrl).isNotNull();
        assertThat(agentsUrl.size()).isEqualTo(1);
    }

    private List<ServiceInstance> createDummyServiceInstance() throws URISyntaxException {
        // Implement this method to create a list of dummy ServiceInstance objects
        List<ServiceInstance> list = new ArrayList<>();
        DefaultServiceInstance dsi = new DefaultServiceInstance();
        dsi.setServiceId("http://localhost:8761");
        dsi.setHost(mockBackEnd.getHostName());
        dsi.setPort(mockBackEnd.getPort());
        URI uri = new URI("http://" + mockBackEnd.getHostName() + ":" + mockBackEnd.getPort());
        dsi.setUri(uri);
        list.add(dsi);
        return list;
    }


}