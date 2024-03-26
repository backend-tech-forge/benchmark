package org.benchmarker.bmcontroller.agent;

import java.util.concurrent.ConcurrentHashMap;
import org.benchmarker.bmagent.AgentStatus;
import org.springframework.stereotype.Component;

@Component
public class AgentServerManager {
    private final ConcurrentHashMap<String, AgentStatus> agentsUrl;

    public AgentServerManager() {
        agentsUrl = new ConcurrentHashMap<>();
    }
    public String add(String url) {
        agentsUrl.put(url, AgentStatus.READY);
        return url;
    }

    public void remove(String url) {
        agentsUrl.remove(url);
    }

    public void updateStatus(String url, AgentStatus status) {
        agentsUrl.put(url, status);
    }

    public AgentStatus getStatus(String url) {
        return agentsUrl.get(url);
    }

    public ConcurrentHashMap<String, AgentStatus> getAllAgents() {
        return agentsUrl;
    }

    public boolean isAgentExist(String url) {
        return agentsUrl.containsKey(url);
    }

}
