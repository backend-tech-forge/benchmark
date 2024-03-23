package org.benchmarker.bmagent.sse;

import org.benchmarker.bmagent.pref.ResultManagerService;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.schedule.SchedulerStatus;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.dto.TestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.util.initialize.MockServer;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SseManageServiceTest extends MockServer {
    private IScheduledTaskService scheduledTaskService;
    private ResultManagerService resultManagerService;

    private SseManageService sseManageService;

    @BeforeEach
    void setUpEach() {

        scheduledTaskService = new ScheduledTaskService();
        resultManagerService = new ResultManagerService();

        WebClient webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        sseManageService = new SseManageService(scheduledTaskService, resultManagerService, webClient);
    }

    @Test
    @DisplayName("sse 시작 시 SseEmitter와 ScheduledTask가 시작되고 셧다운 시, ScheduledTask 와 emitter 가 종료된다")
    void start_ShouldStartSseEmitterAndScheduledTask() throws InterruptedException {
        // given
        Long id = 1L;
        TestResult resultStub = TestResult.builder()
                .startedAt(String.valueOf(LocalDateTime.now()))
                .finishedAt(String.valueOf(LocalDateTime.now()))
                .totalErrors(0)
                .totalRequests(1)
                .statusCode(200)
                .tpsAverage(10.0)
                .mttfbAverage(9.0)
                .build();
        resultManagerService.save(id, resultStub);

        TemplateInfo request = TemplateInfo.builder()
                .name("테스트 템플릿")
                .url(mockBackEnd.url("/").toString())
                .method("get")
                .description("테스트 진행 중입니다.")
                .build();

        // when
        addMockResponse(new SseEmitter(), 1);
        SseEmitter result = sseManageService.start(id, request);

        // then
        assertThat(result).isNotNull();
        assertThat(scheduledTaskService.getStatus().get(id)).isEqualTo(SchedulerStatus.RUNNING);

        sseManageService.stop(id);
        assertThat(scheduledTaskService.getStatus().get(id)).isNull();
    }
}
