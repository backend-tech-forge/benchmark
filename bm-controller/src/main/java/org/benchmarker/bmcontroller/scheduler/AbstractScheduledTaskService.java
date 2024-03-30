package org.benchmarker.bmcontroller.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Abstract class for the scheduled task service
 */
public abstract class AbstractScheduledTaskService implements IScheduledTaskService {

    /**
     * Map of scheduler id to the scheduler
     */
    protected final Map<Long, ScheduledExecutorService> schedulers = new ConcurrentHashMap<>();
    protected final Map<Long, Map<String, ScheduledExecutorService>> schedulerChild = new ConcurrentHashMap<>();
}
