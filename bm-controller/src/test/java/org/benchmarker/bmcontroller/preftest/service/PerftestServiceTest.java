package org.benchmarker.bmcontroller.preftest.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.bmcontroller.MockServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class PerftestServiceTest extends MockServer {
    @Test
    void testExecutePerformanceTest() {
        String url = mockBackEnd.url("/").toString();
        WebClient webClient = WebClient.create(url);
        PerftestService perftestService = new PerftestService();

        // sse 반환 이벤트 stubbing
        CommonTestResult result = RandomUtils.generateRandomTestResult();
        addMockResponseSSE(result);

        TemplateInfo templateInfo = TemplateInfo.builder()
            .url(url)
            .build();

        Flux<ServerSentEvent<CommonTestResult>> eventStream = perftestService.executePerformanceTest(1,
            "start", webClient, templateInfo);

        StepVerifier.create(eventStream)
            .expectNextMatches(event -> {
                assertThat(event.data()).isEqualTo(result);
                return event.data() != null;
            })
            .thenCancel()
            .verify();

    }
}