package org.benchmarker.bmagent.sse;

import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.dto.TestResult;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.bmagent.pref.ResultManagerService;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.schedule.SchedulerStatus;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

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
        TestResult resultStub = RandomUtils.generateRandomTestResult();
        resultManagerService.save(id, resultStub);

        // when
        SseEmitter result = sseManageService.start(id, new TemplateInfo());

        // then
        assertThat(result).isNotNull();
        assertThat(scheduledTaskService.getStatus().get(id)).isEqualTo(SchedulerStatus.RUNNING);

        sseManageService.stop(id);
        assertThat(scheduledTaskService.getStatus().get(id)).isNull();
    }
}
