package org.benchmarker.bmagent.initializer;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmagent.consts.SystemSchedulerConst;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.status.AgentStatusManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Initializer implements CommandLineRunner {

    private final ScheduledTaskService scheduledTaskService;
    private final AgentStatusManager agentStatusManager;

    @Override
    public void run(String... args) throws Exception {
        // cpu, memory usage checker
        scheduledTaskService.startChild(SystemSchedulerConst.systemSchedulerId,
            SystemSchedulerConst.systemUsageSchedulerName, agentStatusManager::updateStats, 0, 1,
            TimeUnit.SECONDS);
    }
}
