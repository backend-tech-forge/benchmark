package org.benchmarker.bmcontroller.preftest.controller;


import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcontroller.agent.AgentServerManager;
import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.preftest.common.TestInfo;
import org.benchmarker.bmcontroller.preftest.service.PerftestService;
import org.benchmarker.bmcontroller.template.service.ITestResultService;
import org.benchmarker.bmcontroller.template.service.ITestTemplateService;
import org.benchmarker.bmcontroller.template.service.TestExecutionService;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final TestExecutionService testExecutionService;

    @GetMapping("/groups/{group_id}/templates/{template_id}")
    @PreAuthorize("hasRole('USER')")
    public String getTest(@PathVariable("group_id") String groupId,
        @PathVariable("template_id") Integer templateId, Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size)
        throws Exception {
        String userId = userContext.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(page, size);

        model.addAttribute("groupId", groupId);
        model.addAttribute("templateId", templateId);
        TemplateInfo templateInfo = testTemplateService.getTemplateInfo(userId, templateId);
        model.addAttribute("template", templateInfo);

        Page<TestInfo> testInfosPageable = testExecutionService.getTestInfosPageable(pageable,
            templateId);
        model.addAttribute("testCurrentPage", pageable.getPageNumber());
        model.addAttribute("testTotalPages", testInfosPageable.getTotalPages());
        model.addAttribute("testTable", testInfosPageable.getContent());
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
        String testId = UUID.randomUUID().toString();
        TestInfo testInfo = TestInfo.builder().testId(testId).groupId(groupId)
            .templateId(templateId).build();

        if (action.equals("stop")) {
            serverUrl = agentServerManager.getAgentMapped().get(Long.valueOf(templateId));
            agentServerManager.removeTemplateRunnerAgent(Long.valueOf(templateId));
            log.info("stop to " + serverUrl);
        }
        if (action.equals("start")) {
            String runningTestId = perftestService.isRunning(testInfo);
            if (runningTestId != null) {
                throw new GlobalException(ErrorCode.ALREADY_RUNNING, "template is already running in testId " + runningTestId);
            }
            // init : save testExecution
            testExecutionService.init(testInfo);
            serverUrl = agentServerManager.getReadyAgent().orElseThrow(() ->
                new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR)).getServerUrl();
            agentServerManager.addTemplateRunnerAgent(Long.valueOf(templateId), serverUrl);
            perftestService.saveRunning(testInfo);
        }

        TemplateInfo templateInfo = testTemplateService.getTemplateInfo(userId, templateId);
        Flux<ServerSentEvent<CommonTestResult>> eventStream = perftestService.executePerformanceTest(
            templateId, groupId, action, WebClient.create(serverUrl), templateInfo);

        handleEvent(eventStream, action, userId, testInfo);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/groups/{group_id}/templates/{template_id}/tests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TestInfo>> getTestExecutions(
        @PathVariable("group_id") String groupId,
        @PathVariable("template_id") Integer templateId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            testExecutionService.getTestInfosList(pageable, templateId));
    }

    /**
     * Handling event stream
     * @param eventStream
     * @param action
     * @param userId
     * @param testInfo
     */
    private void handleEvent(Flux<ServerSentEvent<CommonTestResult>> eventStream, String action, String userId,
        TestInfo testInfo) {
        eventStream
            .doOnComplete(() -> {
                if (!action.equals("stop")) {
                    perftestService.removeRunning(testInfo);
                    log.info("Test completed! {}", testInfo.getTemplateId());
                    messagingTemplate.convertAndSend("/topic/" + userId, "test complete");
                }
            })
            .subscribe(event -> {
                    CommonTestResult commonTestResult = event.data();
                    // 결과 저장
                    CommonTestResult saveReturnResult = testResultService.resultSaveAndReturn(
                            commonTestResult, testInfo)
                        .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));
                    testExecutionService.updateAgentStatus(testInfo.getTestId(),
                        commonTestResult.getTestStatus());
                    messagingTemplate.convertAndSend("/topic/" + testInfo.getGroupId() + "/" + testInfo.getTemplateId(),
                        saveReturnResult);
                },
                error -> {
                    testExecutionService.updateAgentStatus(testInfo.getTestId(),
                        AgentStatus.UNKNOWN);
                    perftestService.removeRunning(testInfo);
                    log.error("Error receiving SSE: {}", error.getMessage());
                });
    }

}