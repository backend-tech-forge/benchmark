package org.benchmarker.bmcontroller.preftest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.bmcontroller.template.service.ITestTemplateService;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.springframework.core.ParameterizedTypeReference;
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
    private final UserContext userContext;
    private final String agentUrl = "http://localhost:8081";

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

        ParameterizedTypeReference<ServerSentEvent<CommonTestResult>> typeReference =
            new ParameterizedTypeReference<ServerSentEvent<CommonTestResult>>() {
            };

        WebClient webClient = WebClient.create(agentUrl);
        // TODO : template 정보를 조회해서 전송해야합니다.
        TemplateInfo templateInfo = testTemplateService.getTemplateInfo(userId, templateId);

        Flux<ServerSentEvent<CommonTestResult>> eventStream = webClient.post()
            .uri("/api/templates/{templateId}?action={action}", templateId, action)
            .bodyValue(templateInfo)
            .retrieve()
            .bodyToFlux(typeReference)
            .log();

        eventStream
            .doOnComplete(() -> {
                if (action.equals("stop")) {
                    log.info("Test completed! {}", action);
                    messagingTemplate.convertAndSend("/topic/" + userId, "test started!");
                }
            })
            .subscribe(event -> {
                    CommonTestResult commonTestResult = event.data();
                    messagingTemplate.convertAndSend("/topic/" + groupId + "/" + templateId,
                        commonTestResult);
                },
                error -> {
                    log.error("Error receiving SSE: {}", error.getMessage());
                });

        return ResponseEntity.ok().build();
    }
}