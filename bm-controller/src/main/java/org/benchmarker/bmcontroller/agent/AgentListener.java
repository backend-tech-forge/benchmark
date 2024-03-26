package org.benchmarker.bmcontroller.agent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/api/endpoint")
    public ResponseEntity agent(HttpServletRequest request) {
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
        String url = agentServerManager.add(request.getRemoteAddr()+":"+request.getRemotePort());
        return ResponseEntity.ok(url + " is added successfully");
    }
}
