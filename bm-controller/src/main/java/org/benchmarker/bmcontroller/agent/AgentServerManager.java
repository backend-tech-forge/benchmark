package org.benchmarker.bmcontroller.agent;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmagent.AgentStatus;
import org.springframework.stereotype.Component;

@Component
@Getter
@Slf4j
public class AgentServerManager {
    private final ConcurrentHashMap<String, AgentInfo> agentsUrl = new ConcurrentHashMap<>();
    private final HashMap<Long, String> agentMapped = new HashMap<>();
    public void add(String url, AgentInfo agentInfo) {
        agentsUrl.put(url, agentInfo);
    }
    public void update(String url, AgentInfo agentInfo){
        if (agentsUrl.get(agentInfo.getServerUrl())==null){
            log.error("cannot find agent");
            return;
        }
        agentsUrl.put(agentInfo.getServerUrl(), agentInfo);
    }

    public Optional<AgentInfo> getReadyAgent(){
        for (AgentInfo agentInfo : agentsUrl.values()){
            if (agentInfo.getStatus()== AgentStatus.READY){
                return Optional.of(agentInfo);
            }
        }
        return Optional.empty();
    }

    public void removeAgent(String url){
        agentsUrl.remove(url);
    }

    public void addTemplateRunnerAgent(Long id, String url){
        agentMapped.put(id,url);
    }

    public void removeTemplateRunnerAgent(Long id){
        String url = agentMapped.get(id);
        if (url != null){
            agentMapped.remove(id);
            agentsUrl.remove(url);
        }
    }

}
