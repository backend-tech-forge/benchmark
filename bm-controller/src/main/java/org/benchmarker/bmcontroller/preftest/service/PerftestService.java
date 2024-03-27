package org.benchmarker.bmcontroller.preftest.service;

import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class PerftestService {

    /**
     * Execute a performance test request to the bm-agent API and receive intermediate results via
     * Server-Sent Events (SSE).
     *
     * @param templateId
     * @param action
     * @param webClient
     * @param templateInfo
     * @return Flux {@link ServerSentEvent} {@link CommonTestResult}
     */
    public Flux<ServerSentEvent<CommonTestResult>> executePerformanceTest(Integer templateId,
        String action, WebClient webClient, TemplateInfo templateInfo) {
        ParameterizedTypeReference<ServerSentEvent<CommonTestResult>> typeReference =
            new ParameterizedTypeReference<ServerSentEvent<CommonTestResult>>() {
            };
        return webClient.post()
            .uri("/api/templates/{templateId}?action={action}", templateId, action)
            .bodyValue(templateInfo)
            .retrieve()
            .bodyToFlux(typeReference)
            .log();
    }
}
