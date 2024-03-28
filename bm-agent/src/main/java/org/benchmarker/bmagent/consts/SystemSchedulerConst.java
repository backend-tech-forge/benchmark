package org.benchmarker.bmagent.consts;

public interface SystemSchedulerConst {

    /**
     * System scheduler will run in this ID
     */
    Long systemSchedulerId = -100L;
    String systemUsageSchedulerName = "cpu-memory-usage-update";
    Integer connectControllerTimeout = 10; // seconds
    Integer connectionFailedLimit = 50;


}
