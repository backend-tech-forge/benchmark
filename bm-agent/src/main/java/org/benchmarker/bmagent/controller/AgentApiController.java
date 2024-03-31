package org.benchmarker.bmagent.controller;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmagent.schedule.SchedulerStatus;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmagent.service.ISseManageService;
import org.benchmarker.bmagent.status.AgentStatusManager;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Main RESTAPI endpoint
 *
 * <p>
 *     bm-controller will send request to here for performance testing
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AgentApiController {

    private final ISseManageService sseManageService;
    private final IScheduledTaskService scheduledTaskService;
    private final AgentStatusManager agentStatusManager;

    /**
     * support sse for the given id
     *
     * @param templateId Long
     * @param action     String
     * @return SseEmitter
     */
    @PostMapping("/groups/{group_id}/templates/{template_id}")
    public SseEmitter manageSSE(@PathVariable("template_id") Long templateId,
        @PathVariable("group_id") String groupId,
        @RequestParam("action") String action, @RequestBody TemplateInfo templateInfo)
        throws IOException {
        log.info(templateInfo.toString());

        if (action.equals("start")) {
            agentStatusManager.getAndUpdateStatusIfReady(
                AgentStatus.TESTING).orElseThrow(() -> new RuntimeException("agent is not ready"));
            return sseManageService.start(templateId, groupId, templateInfo);
        } else {
            sseManageService.stopSign(templateId);
            return null;
        }
    }

    /**
     * Get the status of all schedulers
     *
     * @return Map of scheduler id, status
     */
    @GetMapping("/scheduler/status")
    public ResponseEntity<Map<Long, SchedulerStatus>> getSchedulersStatus() {
        return ResponseEntity.ok(scheduledTaskService.getStatus());
    }

    @GetMapping("/status")
    public AgentInfo getStatus() throws UnknownHostException {
        log.info("Check current status");
        InetAddress localHost = InetAddress.getLocalHost();
        String serverAddress = localHost.getHostAddress();
        String scheme = "http"; // Assuming it's always HTTP when accessed locally
        int serverPort = 8081; // Assuming default port is 8080

        Set<Long> longs = scheduledTaskService.getStatus().keySet();

        String agentServerUrl = scheme + "://" + serverAddress + ":" + serverPort;
        AgentInfo info = AgentInfo.builder()
            .templateId(longs)
            .cpuUsage(agentStatusManager.getCpuUsage())
            .memoryUsage(agentStatusManager.getMemoryUsage())
            .startedAt(agentStatusManager.getStartedAt())
            .serverUrl(agentServerUrl)
            .status(agentStatusManager.getStatus().get())
            .build();
        log.info(info.toString());
        return info;
    }
}

