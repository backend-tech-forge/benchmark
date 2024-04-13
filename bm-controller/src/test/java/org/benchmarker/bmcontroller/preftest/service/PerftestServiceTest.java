package org.benchmarker.bmcontroller.preftest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.UUID;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.bmcontroller.MockServer;
import org.benchmarker.bmcontroller.preftest.common.TestInfo;
import org.benchmarker.bmcontroller.preftest.repository.RunningTestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Testcontainers
@SpringBootTest
@ExtendWith(RedisInitializer.class)
class PerftestServiceTest extends MockServer {

    @Autowired
    private RunningTestRepository runningTestRepository;
    @Test
    void testExecutePerformanceTest() {
        String url = mockBackEnd.url("/").toString();
        WebClient webClient = WebClient.create(url);
        PerftestService perftestService = new PerftestService(runningTestRepository);

        // sse 반환 이벤트 stubbing
        CommonTestResult result = RandomUtils.generateRandomTestResult();
        addMockResponseSSE(result);

        TemplateInfo templateInfo = TemplateInfo.builder()
            .url(url)
            .build();

        Flux<ServerSentEvent<CommonTestResult>> eventStream = perftestService.executePerformanceTest(1,
            "groupId","start", webClient, templateInfo);

        StepVerifier.create(eventStream)
            .expectNextMatches(event -> {
                assertThat(event.data()).isEqualTo(result);
                return event.data() != null;
            })
            .thenCancel()
            .verify();

    }

    @Test
    @DisplayName("테스트 저장 및 진행상황 확인")
    void testExecute() {
//        String groupId = "1";
//        Integer templateId = 1;
//        String testId = UUID.randomUUID().toString();
//        TestInfo testInfo = TestInfo.builder().testId(testId).templateId(templateId)
//            .groupId(groupId).build();
//
//        // expect
//        HashSet<TestInfo> expectTemplate = new HashSet<>();
//        expectTemplate.add(testInfo);
//
//        PerftestService perftestService = new PerftestService(runningTestRepository);
//        perftestService.saveRunning(testInfo);
//
//        // 저장 확인
//        String running = perftestService.isRunning(testInfo);
//        assertThat(running).isNotEmpty();
    }

}