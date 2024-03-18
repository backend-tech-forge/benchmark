package org.benchmark.bmagent.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.benchmark.bmagent.schedule.SchedulerStatus;

/**
 * Interface for the SSE service
 */
public interface IScheduledTaskService {

    /**
     * shutdown the scheduler with the given id
     *
     * @param id Long
     */
    void shutdown(Long id);

    /**
     * run the scheduler with the given id
     *
     * @param id Long
     * @param runnable Runnable
     * @param delay long
     * @param period long
     * @param timeUnit TimeUnit
     */
    void start(Long id, Runnable runnable, long delay, long period, TimeUnit timeUnit);

    /**
     * Get the status of all the schedulers
     *
     * @return a map of scheduler id, {@link SchedulerStatus}
     */
    Map<Long, SchedulerStatus> getStatus();
}
