package org.benchmarker.bmagent.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.benchmarker.bmagent.schedule.SchedulerStatus;

/**
 * Abstract class for the scheduled task service
 */
public abstract class AbstractScheduledTaskService implements IScheduledTaskService {

    /**
     * Map of scheduler id to the scheduler
     */
    protected final Map<Long, ScheduledExecutorService> schedulers = new ConcurrentHashMap<>();
    protected final Map<Long, Map<String, ScheduledExecutorService>> schedulerChild = new ConcurrentHashMap<>();

    /**
     * Get the status of all the schedulers
     *
     * @return a map of scheduler id to status
     */
    public Map<Long, SchedulerStatus> getStatus() {
        Map<Long, SchedulerStatus> statusMap = new ConcurrentHashMap<>();
        for (Map.Entry<Long, ScheduledExecutorService> entry : schedulers.entrySet()) {
            if (entry.getValue().isShutdown()) {
                statusMap.put(entry.getKey(), SchedulerStatus.SHUTDOWN);
            } else if (entry.getValue().isTerminated()) {
                statusMap.put(entry.getKey(), SchedulerStatus.TERMINATED);
            } else {
                statusMap.put(entry.getKey(), SchedulerStatus.RUNNING);
            }
        }
        return statusMap;
    }
}
