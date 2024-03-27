package org.benchmarker.bmagent.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.benchmarker.bmagent.schedule.SchedulerStatus;

/**
 * This service allows you to schedule and manage tasks to be executed at specific intervals.
 *
 * <p>
 * Each task is identified by a unique ID, enabling you to easily find, start, and
 * shutdown scheduled tasks. It is crucial to ensure that <strong>IDs are unique across all scheduled
 * tasks.</strong>
 *
 *
 * </p>
 */
public interface IScheduledTaskService {

    /**
     * Shutdowns the scheduler associated with the given ID.
     *
     * <p>
     * The ID serves as the key to identify the scheduler to be shutdown.
     * </p>
     *
     * @param id Unique identifier of the scheduler.
     */
    void shutdown(Long id);

    /**
     * Checks if a child scheduler exists for the given ID.
     *
     * @param id The ID to check for the existence of a child scheduler.
     * @return true if a child scheduler exists, otherwise false.
     */
    void start(Long id, Runnable runnable, long delay, long period, TimeUnit timeUnit);

    /**
     * Checks if any scheduler (parent or child) exists for the given ID.
     *
     * @param id The ID to check for the existence of any scheduler.
     * @return true if any scheduler exists, otherwise false.
     */
    boolean isChildExist(Long id);

    /**
     * check if child || parent scheduler exist with given id
     *
     * @param id
     * @return boolean
     */
    boolean hasAnyScheduler(Long id);

    /**
     * Starts a child scheduler with an additional name for the given ID.
     *
     * @param id            The ID associated with the parent scheduler.
     * @param schedulerName Name of the child scheduler.
     * @param runnable      Task to be executed.
     * @param delay         Delay before the first execution.
     * @param period        Period between successive executions.
     * @param timeUnit      TimeUnit for the delay and period.
     */
    void startChild(Long id, String schedulerName, Runnable runnable, long delay, long period, TimeUnit timeUnit);

    /**
     * Get the status of all the schedulers
     *
     * @return a map of scheduler id, {@link SchedulerStatus}
     */
    Map<Long, SchedulerStatus> getStatus();
}
