package org.benchmarker.bmagent.schedule;

import java.util.Map;
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
        ScheduledExecutorService scheduler = schedulers.get(id);
        if (scheduler != null) {
            log.info("[id:{}] scheduler shutdown", id);
            if (schedulerChild.get(id) != null) {
                for (ScheduledExecutorService child : schedulerChild.get(id).values()) {
                    child.shutdown();
                }
            }
            scheduler.shutdown();
        }
        schedulers.remove(id);
        schedulerChild.remove(id);
    }

    @Override
    public void start(Long id, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        if (schedulers.get(id) != null) {
            ScheduledExecutorService a = schedulers.get(id);
            String msg = "already id: " + id + " is assigned in major scheduler";
            log.error(msg);
            throw new IllegalThreadStateException(msg);
        }
        ;
        log.info("[id:{}] scheduler start", id);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        schedulers.put(id, scheduler);
        scheduler.scheduleAtFixedRate(runnable, delay, period, timeUnit);
    }

    // deprecated
    @Override
    public boolean isChildExist(Long id) {
        return schedulerChild.containsKey(id);
    }

    // deprecated
    @Override
    public boolean hasAnyScheduler(Long id) {
        return schedulerChild.containsKey(id) || schedulers.containsKey(id);
    }

    @Override
    public void startChild(Long id, String schedulerName, Runnable runnable, long delay,
        long period, TimeUnit timeUnit) {
        log.info("[id:{}] child scheduler start", id);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        schedulerChild.put(id, Map.of(schedulerName, scheduler));
        scheduler.scheduleAtFixedRate(runnable, delay, period, timeUnit);
    }
}
