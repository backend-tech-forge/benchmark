package org.benchmarker.bmagent.consts;

import java.util.Arrays;
import java.util.List;

/**
 * Performance Test Constants
 */
public interface PreftestConsts {

    /**
     * Required when measuring percentile of TPS and MTTFB(Mean Time To First Byte)
     */
    List<Double> percentiles = Arrays.asList(50D, 90D, 95D, 99D, 99.9D);

    /**
     * If performance test has error rate exceed {@link PreftestConsts#errorLimitRate}, instantly
     * shutdown all scheduler and emit event to bm-controller
     */
    Double errorLimitRate = 50D;

    /**
     * ErrorLimit Lookup process will be started after this time
     */
    int errorLimitCheckDelay = 10;

    /**
     * ErrorLimit Lookup process period
     */
    int errorLimitCheckPeriod = 5;

}
