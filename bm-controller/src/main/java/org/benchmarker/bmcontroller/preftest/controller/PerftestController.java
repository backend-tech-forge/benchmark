package org.benchmarker.bmcontroller.preftest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcontroller.agent.AgentServerManager;
import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.preftest.service.PerftestService;
import org.benchmarker.bmcontroller.template.service.ITestResultService;
import org.benchmarker.bmcontroller.template.service.ITestTemplateService;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Controller
@GlobalControllerModel
@Slf4j
@RequiredArgsConstructor
public class PerftestController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ITestTemplateService testTemplateService;

    private final PerftestService perftestService;

    private final UserContext userContext;

    private final AgentServerManager agentServerManager;

    private final ITestResultService testResultService;

    @GetMapping("/groups/{group_id}/templates/{template_id}")
    @PreAuthorize("hasRole('USER')")
    public String getTest(@PathVariable("group_id") String groupId,
        @PathVariable("template_id") Integer templateId, Model model) throws Exception {
        String userId = userContext.getCurrentUser().getId();

        model.addAttribute("groupId", groupId);
        model.addAttribute("templateId", templateId);
        TemplateInfo templateInfo = testTemplateService.getTemplateInfo(userId, templateId);
        model.addAttribute("template", templateInfo);

        return "template/info"; // Thymeleaf 템플릿의 이름
    }

    @PostMapping("/api/groups/{group_id}/templates/{template_id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity send(@PathVariable("group_id") String groupId,
        @PathVariable("template_id") Integer templateId,
        @RequestParam(value = "action") String action) throws Exception {
        log.info("Send action: {}", action);
        String userId = userContext.getCurrentUser().getId();
        String serverUrl = "";
        if (action.equals("stop")) {
            serverUrl = agentServerManager.getAgentMapped().get(Long.valueOf(templateId));
            agentServerManager.removeTemplateRunnerAgent(Long.valueOf(templateId));

            log.info("stop to " + serverUrl);
        }else{
            if (perftestService.isRunning(groupId, templateId)){
                log.warn("template is already running");
                throw new GlobalException(ErrorCode.ALREADY_RUNNING);
            }
            serverUrl = agentServerManager.getReadyAgent().orElseThrow(() ->
                new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR)).getServerUrl();

            agentServerManager.addTemplateRunnerAgent(Long.valueOf(templateId), serverUrl);
            log.info("send to " + serverUrl);
        }
        WebClient webClient = WebClient.create(serverUrl);

        TemplateInfo templateInfo = testTemplateService.getTemplateInfo(userId, templateId);
        Flux<ServerSentEvent<CommonTestResult>> eventStream = perftestService.executePerformanceTest(
            templateId, groupId, action, webClient, templateInfo);
        perftestService.saveRunning(groupId, templateId);

        eventStream
            .doOnComplete(() -> {
                perftestService.removeRunning(groupId,templateId);

                if (action.equals("stop")) {
                    log.info("Test completed! {}", templateId);
                    messagingTemplate.convertAndSend("/topic/" + userId, "test complete");
                }
            })
            .subscribe(event -> {
                    CommonTestResult commonTestResult = event.data();

                    // 결과 저장
                    log.info("Start save Result");
                    CommonTestResult saveReturnResult = testResultService.resultSaveAndReturn(commonTestResult)
                            .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));
                    messagingTemplate.convertAndSend("/topic/" + groupId + "/" + templateId, saveReturnResult);
                    log.info("End save Result");
                },
                error -> {
                    log.error("Error receiving SSE: {}", error.getMessage());
                });

        return ResponseEntity.ok().build();
    }

}