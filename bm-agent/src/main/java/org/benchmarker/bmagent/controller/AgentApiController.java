package org.benchmarker.bmagent.controller;


import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.schedule.SchedulerStatus;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmagent.service.ISseManageService;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AgentApiController {

    private final ISseManageService sseManageService;
    private final IScheduledTaskService scheduledTaskService;

    /**
     * support sse for the given id
     *
     * @param templateId Long
     * @param action     String
     * @return SseEmitter
     */
    @PostMapping("/templates/{template_id}")
    public SseEmitter manageSSE(@PathVariable("template_id") Long templateId,
        @RequestParam("action") String action, @RequestBody(required = false) TemplateInfo templateInfo) {

        if (action.equals("start")) {
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
    @GetMapping("/status")
    public ResponseEntity<Map<Long, SchedulerStatus>> getSchedulersStatus() {
        return ResponseEntity.ok(scheduledTaskService.getStatus());
    }
}

