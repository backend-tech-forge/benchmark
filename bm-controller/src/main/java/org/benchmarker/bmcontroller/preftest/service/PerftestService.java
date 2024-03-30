package org.benchmarker.bmcontroller.preftest.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Getter
@Slf4j
public class PerftestService {

    private ConcurrentHashMap<String, Set<Integer>> runningTemplates = new ConcurrentHashMap<>();

    public void saveRunning(String groupId, Integer templateId) {
        Set<Integer> templates = runningTemplates.get(groupId);
        if (templates != null) {
            templates.add(templateId);
        } else {
            HashSet<Integer> temp = new HashSet<Integer>();
            temp.add(templateId);
            runningTemplates.put(groupId, temp);
        }
        log.info(runningTemplates.toString());
    }

    public void removeRunning(String groupId, Integer templateId) {
        Set<Integer> templates = runningTemplates.get(groupId);
        if (templates != null) {
            templates.remove(templateId);
            if (templates.size()==0){
                runningTemplates.remove(groupId);
            }
        }
        ;
    }

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
        String groupId,
        String action, WebClient webClient, TemplateInfo templateInfo) {
        ParameterizedTypeReference<ServerSentEvent<CommonTestResult>> typeReference =
            new ParameterizedTypeReference<ServerSentEvent<CommonTestResult>>() {
            };
        return webClient.post()
            .uri("/api/groups/{groupId}/templates/{templateId}?action={action}", groupId,
                templateId, action)
            .bodyValue(templateInfo)
            .retrieve()
            .bodyToFlux(typeReference)
            .log(); // TODO : remove
    }
}
