package org.benchmarker.bmagent.sse;

import static org.assertj.core.api.Assertions.assertThat;

import org.benchmarker.bmagent.pref.ResultManagerService;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.schedule.SchedulerStatus;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class SseManageServiceTest {
    private IScheduledTaskService scheduledTaskService;
    private ResultManagerService resultManagerService;

    private SseManageService sseManageService;

    @BeforeEach
    void setUp() {
        scheduledTaskService = new ScheduledTaskService();
        resultManagerService = new ResultManagerService();
        sseManageService = new SseManageService(scheduledTaskService, resultManagerService);
    }

    @Test
    @DisplayName("sse 시작 시 SseEmitter와 ScheduledTask가 시작되고 셧다운 시, ScheduledTask 와 emitter 가 종료된다")
    void start_ShouldStartSseEmitterAndScheduledTask() throws InterruptedException {
        // given
        Long id = 1L;
        CommonTestResult resultStub = RandomUtils.generateRandomTestResult();
        resultManagerService.save(id, resultStub);

        // when
        SseEmitter result = sseManageService.start(id, new TemplateInfo());

        // then
        assertThat(result).isNotNull();
        assertThat(scheduledTaskService.getStatus().get(id)).isEqualTo(SchedulerStatus.RUNNING);

        sseManageService.stop(id);
        assertThat(scheduledTaskService.getStatus().get(id)).isNull();
    }

    @Test
    @DisplayName("sse 시작 시, id 가 중복된다면(기존 emitter 가 존재하면) null 를 즉시 반환한다")
    void startAndShutdown() throws InterruptedException {
        // given
        Long id = 1L;
        CommonTestResult resultStub = RandomUtils.generateRandomTestResult();
        resultManagerService.save(id, resultStub);
        SseEmitter result = sseManageService.start(id, new TemplateInfo());

        // when
        SseEmitter res = sseManageService.start(id, new TemplateInfo());

        // then
        assertThat(res).isNull();
        sseManageService.stop(id);
    }

    @Test
    @DisplayName("stop 을 호출하기 전에 이미 종료된 emitter 를 stop 할 경우, 아무 동작도 하지 않는다")
    void stop_ShouldDoNothingIfEmitterAlreadyStopped() throws InterruptedException {
        // given
        Long id = 1L;
        CommonTestResult resultStub = RandomUtils.generateRandomTestResult();
        resultManagerService.save(id, resultStub);
        sseManageService.start(id, new TemplateInfo());
        sseManageService.stop(id);

        // when
        sseManageService.stop(id);

        // then
        assertThat(scheduledTaskService.getStatus().get(id)).isNull();
    }

}
