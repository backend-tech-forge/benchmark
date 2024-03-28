package org.benchmarker.bmagent.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduledTaskServiceTest {

    private IScheduledTaskService scheduledTaskService;

    @BeforeEach
    void setUp() {
        scheduledTaskService = new ScheduledTaskService();
    }

    @Test
    @DisplayName("ScheduledTask 시작 및 셧다운 시 status null 테스트")
    void test2() {
        // given
        Long id = 1L;
        scheduledTaskService.start(id, mock(Runnable.class), 0, 1, TimeUnit.SECONDS);

        // when
        scheduledTaskService.shutdown(id);

        // then
        assertThat(scheduledTaskService.getStatus().get(id)).isNull();
    }

    @Test
    @DisplayName("ScheduledTask 중복 시작 제어")
    void test23() {
        // given
        Long id = 1L;
        scheduledTaskService.start(id, mock(Runnable.class), 0, 1, TimeUnit.SECONDS);

        // when & then
        String message = assertThrows((IllegalThreadStateException.class), () -> {
            // 중복 시작
            scheduledTaskService.start(id, mock(Runnable.class), 0, 1, TimeUnit.SECONDS);
        }).getMessage();
        assertThat(message).contains("already id");
        scheduledTaskService.shutdown(id);
    }

    @Test
    @DisplayName("ScheduledTask child 스케줄러 셧다운 테스트")
    void test234() {
        // given
        Long id = 1L;
        String schedulerName = "my-child-process";
        scheduledTaskService.startChild(id, schedulerName, mock(Runnable.class), 0, 1,
            TimeUnit.SECONDS);

        // when
        scheduledTaskService.shutdown(id);

        // then
        Map<String, ScheduledExecutorService> remainSchedulers = scheduledTaskService.getSchedulers(id);
        assertThat(remainSchedulers).isEmpty();
    }

}
