package org.benchmarker.bmagent.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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

//    @Test
//    @DisplayName("ScheduledTask 시작 테스트")
//    void test1() {
//        // given & when
//        Long id = 1L;
//        scheduledTaskService.start(id, mock(Runnable.class), 0, 1, TimeUnit.SECONDS);
//
//        // then
//        assertThat(scheduledTaskService.getStatus().get(id)).isEqualTo(SchedulerStatus.RUNNING);
//    }

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

}
