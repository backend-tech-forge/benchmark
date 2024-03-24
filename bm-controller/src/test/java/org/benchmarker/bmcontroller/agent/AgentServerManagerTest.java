package org.benchmarker.bmcontroller.agent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentHashMap;
import org.benchmarker.bmagent.AgentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AgentServerManagerTest {

    @Autowired
    private AgentServerManager agentServerManager;
    private String url = "http://localhost:8080";

    @Test
    @DisplayName("AgentServerManager 테스트")
    void test() {
        assertThat(agentServerManager.getAllAgents()).isEmpty();

        // Add agent
        agentServerManager.add(url);
        assertThat(agentServerManager.isAgentExist(url)).isTrue();
        assertThat(agentServerManager.getStatus(url)).isEqualTo(AgentStatus.READY);
        ConcurrentHashMap<String, AgentStatus> allAgents = agentServerManager.getAllAgents();
        assertThat(allAgents.size()).isEqualTo(1);
        assertThat(allAgents.containsKey(url)).isTrue();
        assertThat(allAgents.get(url)).isEqualTo(AgentStatus.READY);

        // Update agent status
        agentServerManager.updateStatus(url, AgentStatus.TESTING);
        assertThat(agentServerManager.getStatus(url)).isEqualTo(AgentStatus.TESTING);

        // Remove agent
        agentServerManager.remove(url);
        assertThat(agentServerManager.isAgentExist(url)).isFalse();
        assertThat(agentServerManager.getStatus(url)).isNull();

        // Check getAllAgents
        allAgents = agentServerManager.getAllAgents();
        assertThat(allAgents.size()).isEqualTo(0);
        assertThat(allAgents.containsKey(url)).isFalse();
        assertThat(allAgents.get(url)).isNull();
    }

}