package org.benchmarker.bmagent.consts;

/**
 * System Scheduler constants
 */
public interface SystemSchedulerConst {

    /**
     * System scheduler will run in this ID
     */
    Long systemSchedulerId = -100L;

    /**
     * System scheduler's name
     */
    String systemUsageSchedulerName = "cpu-memory-usage-update";

    /**
     * @deprecated since 2024-03-28
     */
    Integer connectControllerTimeout = 10; // seconds

    /**
     * @deprecated since 2024-03-28
     */
    Integer connectionFailedLimit = 50;


}
