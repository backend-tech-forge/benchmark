package org.benchmarker.bmagent.initializer;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.consts.SystemSchedulerConst;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.status.AgentStatusManager;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The Initializer class is responsible for <strong>initializing various components of the
 * application when it starts up</strong>.
 *
 * <p>
 * The Initializer will start system scheduler which recording CPU / MEMORY usage in every seconds
 */
@Component
@Profile("!test")
@Slf4j
@RequiredArgsConstructor
public class Initializer implements CommandLineRunner, ApplicationContextAware {

    private final ScheduledTaskService scheduledTaskService;
    private final AgentStatusManager agentStatusManager;
    private ApplicationContext applicationContext;


    @Override
    public void run(String... args) throws Exception {
        // cpu, memory usage checker
        scheduledTaskService.startChild(SystemSchedulerConst.systemSchedulerId,
            SystemSchedulerConst.systemUsageSchedulerName, agentStatusManager::updateStats, 0, 1,
            TimeUnit.SECONDS);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
