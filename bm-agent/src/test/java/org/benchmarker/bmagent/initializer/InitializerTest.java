package org.benchmarker.bmagent.initializer;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
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

        Map<String, ScheduledExecutorService> schedulers = scheduledTaskService.getSchedulers(
            SystemSchedulerConst.systemSchedulerId);
        assertThat(schedulers).isNotNull();
        assertThat(schedulers.keySet().size()).isEqualTo(1);
        assertThat(schedulers.keySet().toArray()[0]).isEqualTo(SystemSchedulerConst.systemUsageSchedulerName);
        sleep(4000); // wait 2 seconds and scheduler will calculate cpu&memory usage in a seconds
        assertThat(agentStatusManager.getCpuUsage()).isNotNull();
        assertThat(agentStatusManager.getMemoryUsage()).isNotNull();
        System.out.println("cpu usage test : " + agentStatusManager.getCpuUsage());
        System.out.println("memory usage test : " + agentStatusManager.getMemoryUsage());

        assertThat(SystemSchedulerConst.connectControllerTimeout).isEqualTo(10);
        assertThat(SystemSchedulerConst.systemSchedulerId).isEqualTo(-100L);
        assertThat(SystemSchedulerConst.systemUsageSchedulerName).isEqualTo("cpu-memory-usage-update");
        assertThat(SystemSchedulerConst.connectionFailedLimit).isEqualTo(50);
    }
}