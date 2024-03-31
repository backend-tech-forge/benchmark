package org.benchmarker.bmcontroller.preftest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.bmcontroller.MockServer;
import org.junit.jupiter.api.DisplayName;
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
        String groupId = "1";
        Integer templateId = 1;

        // expect
        HashSet<Integer> expectTemplate = new HashSet<>();
        expectTemplate.add(templateId);

        PerftestService perftestService = new PerftestService();
        perftestService.saveRunning(groupId,templateId);

        // 저장 확인
        ConcurrentHashMap<String, Set<Integer>> runningTemplates = perftestService.getRunningTemplates();
        Boolean running = perftestService.isRunning(groupId, templateId);

        assertThat(runningTemplates.get(groupId)).isEqualTo(expectTemplate);
        assertThat(running).isTrue();

        // 중복저장 시도
        perftestService.saveRunning(groupId,templateId);
    }
    @Test
    @DisplayName("테스트 진행저장 및 확인")
    void testExecute2() {
        // given
        String groupId = "1";
        Integer templateId = 1;
        PerftestService perftestService = new PerftestService();

        // expect
        HashSet<Integer> expectTemplate = new HashSet<>();
        expectTemplate.add(templateId);

        // when
        perftestService.saveRunning(groupId,templateId);

        // then-1
        ConcurrentHashMap<String, Set<Integer>> runningTemplates = perftestService.getRunningTemplates();
        assertThat(runningTemplates.get(groupId)).isEqualTo(expectTemplate);

        // then-2
        Boolean running = perftestService.isRunning(groupId, templateId);
        assertThat(running).isTrue();

        // 중복저장 시도
        perftestService.saveRunning(groupId,templateId);
    }

    @Test
    @DisplayName("테스트 중복저장 시 noOp")
    void testExecute3() {
        // given
        String groupId = "1";
        Integer templateId = 1;
        PerftestService perftestService = new PerftestService();
        perftestService.saveRunning(groupId,templateId);

        // expect
        HashSet<Integer> expectTemplate = new HashSet<>();
        expectTemplate.add(templateId);

        // when
        perftestService.saveRunning(groupId,templateId);

        // then-1
        ConcurrentHashMap<String, Set<Integer>> runningTemplates = perftestService.getRunningTemplates();
        assertThat(runningTemplates.get(groupId)).isEqualTo(expectTemplate);

        // then-2
        Boolean running = perftestService.isRunning(groupId, templateId);
        assertThat(running).isTrue();

        // 중복저장 시도
        perftestService.saveRunning(groupId,templateId);
    }

    @Test
    @DisplayName("테스트 정상제거 시 null 반환")
    void testExecute4() {
        // given
        String groupId = "1";
        Integer templateId = 1;
        PerftestService perftestService = new PerftestService();
        perftestService.saveRunning(groupId,templateId);

        // expect
        HashSet<Integer> expectTemplate = new HashSet<>();
        expectTemplate.add(templateId);

        // when
        perftestService.removeRunning(groupId,templateId);

        // then-1
        ConcurrentHashMap<String, Set<Integer>> runningTemplates = perftestService.getRunningTemplates();
        assertThat(runningTemplates.get(groupId)).isNull();

    }

    @Test
    @DisplayName("테스트 10개 save 및 1개 삭제 및 잔여 9개 확인")
    void testExecute6() {
        // given
        String groupId = "1";
        PerftestService perftestService = new PerftestService();
        List<Integer> templates = new ArrayList<>();

        // when
        for (int i=0;i<10;i++){
            perftestService.saveRunning(groupId,i);
            templates.add(i);
        }
        perftestService.removeRunning(groupId,1);


        // then
        ConcurrentHashMap<String, Set<Integer>> runningTemplates = perftestService.getRunningTemplates();
        assertThat(runningTemplates.get(groupId).size()).isEqualTo(9);
        assertThat(runningTemplates.get(groupId).contains(1)).isFalse();



    }
}