package org.benchmarker.bmagent.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.benchmarker.bmagent.schedule.SchedulerStatus;
import org.benchmarker.bmagent.service.ISseManageService;
import org.benchmarker.util.MockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
public class AgentApiControllerTest extends MockServer {

    @Mock
    private ISseManageService sseManageService;

    @InjectMocks
    private AgentApiController agentApiController;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private MockMvc mockMvc;

    @Test
    @DisplayName("SSE start 통신 테스트")
    public void testStartSSE() throws IOException {
        // given
        SseEmitter mockSseEmitter = Mockito.mock(SseEmitter.class);

        // when
        // sseManageService.start() 메서드의 행동을 설정하는 스텁 설정
        doAnswer(invocation -> {
            // 클라이언트로 데이터를 보내는 stub
            mockSseEmitter.send("Data 1");
            mockSseEmitter.send("Data 2");
            mockSseEmitter.complete();
            return null;
        }).when(sseManageService).start(eq(1L), any());

        // 호출
        agentApiController.manageSSE(1L, "start", null);

        // then
        // SseEmitter 로 전송된 메시지 모두 캡처
        Mockito.verify(mockSseEmitter, times(2)).send(messageCaptor.capture());
        assertEquals("Data 1", messageCaptor.getAllValues().get(0));
        assertEquals("Data 2", messageCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("SSE stop 통신 테스트")
    public void testStopSSE() throws IOException {
        // given
        Long templateId = 1L;

        // when
        agentApiController.manageSSE(templateId, "stop", null);

        // then
        // sseManageService.stop() 메서드가 호출되었는지 검증
        verify(sseManageService, times(1)).stop(eq(templateId));
    }

    @Test
    @DisplayName("스케줄러 상태 체크 테스트")
    public void testCheckSchedulerStatus() throws Exception {
        // given
        WebClient webClient = WebClient.create(mockServer.url("/api/status").toString());
        Map<Long, SchedulerStatus> schedulerStatusMap = new HashMap<>();
        schedulerStatusMap.put(1L, SchedulerStatus.RUNNING);
        schedulerStatusMap.put(2L, SchedulerStatus.SHUTDOWN);
        addMockResponse(schedulerStatusMap);

        // when
        Map<Long, SchedulerStatus> response = webClient.get()
            .uri("/api/status")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<Long, SchedulerStatus>>() {
            })
            .log()
            .block();

        // then
        assertThat(response.get(1L)).isEqualTo(SchedulerStatus.RUNNING);
        assertThat(response.get(2L)).isEqualTo(SchedulerStatus.SHUTDOWN);
    }
}