package org.benchmarker.bmagent.initializer;

import static org.assertj.core.api.Assertions.assertThat;

import org.benchmarker.bmagent.consts.SystemSchedulerConst;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.status.AgentStatusManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InitializerTest {
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    private AgentStatusManager agentStatusManager;
    @Test
    @DisplayName("System scheduler running check")
    void test1() throws Exception {
        Initializer initializer = new Initializer(scheduledTaskService,agentStatusManager);
        initializer.run();

        assertThat(SystemSchedulerConst.connectControllerTimeout).isEqualTo(10);
        assertThat(SystemSchedulerConst.systemSchedulerId).isEqualTo(-100L);
        assertThat(SystemSchedulerConst.systemUsageSchedulerName).isEqualTo("cpu-memory-usage-update");
        assertThat(SystemSchedulerConst.connectionFailedLimit).isEqualTo(50);
    }
}