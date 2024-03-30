package org.benchmarker.bmcontroller.agent;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgentListener {

    private final JwtTokenProvider jwtTokenProvider;
    private final AgentServerManager agentServerManager;

    /**
     * Agent will be added when they send requests with their information
     *
     * @return String
     */
    @PostMapping("/api/agent/register")
    public ResponseEntity agent(HttpServletRequest request, @RequestBody AgentInfo agentInfo) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new GlobalException(ErrorCode.UNAUTHORIZED);
        }

        String jwtToken = jwtTokenProvider.getJwtFromRequest(request);
        boolean isValid = jwtTokenProvider.validateToken(jwtToken);

        if (!isValid) {
            throw new GlobalException(ErrorCode.UNAUTHORIZED);
        }

        // Add agent server url and port to the list
//        agentServerManager.add(agentInfo);
        return ResponseEntity.ok(agentInfo.getServerUrl() + " is added successfully");
    }

    @GetMapping("/api/agents")
    public List<AgentInfo> getAgents(){
        ConcurrentHashMap<String, AgentInfo> agentsUrl = agentServerManager.getAgentsUrl();
        return agentsUrl.values().stream().toList();
    }
}
