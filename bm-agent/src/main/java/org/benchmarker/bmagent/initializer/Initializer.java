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
        log.info("init");
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
