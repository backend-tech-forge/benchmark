package org.benchmarker.bmcontroller.preftest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmark.dto.TestResult;
import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
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
    private final UserContext userContext;
    private final String agentUrl = "http://localhost:8081";

    @GetMapping("/groups/{group_id}/templates/{template_id}")
    @PreAuthorize("hasRole('USER')")
    public String getTest(@PathVariable("group_id") String groupId,
        @PathVariable("template_id") String templateId, Model model) {
        model.addAttribute("groupId", groupId);
        model.addAttribute("templateId", templateId);
        return "template/info"; // Thymeleaf 템플릿의 이름
    }

    @PostMapping("/api/groups/{group_id}/templates/{template_id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity send(@PathVariable("group_id") String groupId,
        @PathVariable("template_id") String templateId,
        @RequestParam(value = "action") String action) {
        log.info("Send action: {}", action);
        String userId = userContext.getCurrentUser().getId();

        ParameterizedTypeReference<ServerSentEvent<TestResult>> typeReference =
            new ParameterizedTypeReference<ServerSentEvent<TestResult>>() {
            };

        WebClient webClient = WebClient.create(agentUrl);

        Flux<ServerSentEvent<TestResult>> eventStream = webClient.post()
            .uri("/api/templates/{template_id}?action={action}", templateId, action)
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
                    TestResult testResult = event.data();
                    messagingTemplate.convertAndSend("/topic/" + groupId + "/" + templateId, testResult);
                },
                error -> {
                    log.error("Error receiving SSE: {}", error.getMessage());
            });

        return ResponseEntity.ok().build();
    }
}