package org.benchmarker.bmagent.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.service.AbstractScheduledTaskService;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ScheduledTaskService extends AbstractScheduledTaskService {

    @Override
    public void shutdown(Long id) {
        ScheduledExecutorService scheduler = schedulers.remove(id);
        if (scheduler != null) {
            log.info("[id:{}] scheduler shutdown", id);
            scheduler.shutdown();
        }
    }

    @Override
    public void start(Long id, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        log.info("[id:{}] scheduler start", id);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        schedulers.put(id, scheduler);
        scheduler.scheduleAtFixedRate(runnable, delay, period, timeUnit);

    }
}
