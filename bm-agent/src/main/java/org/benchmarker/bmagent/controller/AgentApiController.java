package org.benchmarker.bmagent.controller;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


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
    @PostMapping("/templates/{template_id}")
    public SseEmitter manageSSE(@PathVariable("template_id") Long templateId,
        @RequestParam("action") String action, @RequestBody TemplateInfo templateInfo) {
        log.info(templateInfo.toString());
        if (action.equals("start")) {
            agentStatusManager.getAndUpdateStatusIfReady(
                AgentStatus.TESTING).orElseThrow(() -> new RuntimeException("agent is not ready"));
            return sseManageService.start(templateId, templateInfo);
        } else {
            sseManageService.stop(templateId);
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
    public AgentInfo getStatus() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String scheme = request.getScheme(); // http or https
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        String agentServerUrl = scheme + "://" + serverName + ":" + serverPort;

        return AgentInfo.builder()
            .cpuUsage(agentStatusManager.getCpuUsage())
            .memoryUsage(agentStatusManager.getMemoryUsage())
            .startedAt(agentStatusManager.getStartedAt())
            .serverUrl(agentServerUrl)
            .status(agentStatusManager.getStatus().get())
            .build();
    }
}

