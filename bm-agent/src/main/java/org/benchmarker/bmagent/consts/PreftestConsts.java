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
    List<Double> percentiles = Arrays.asList(50D, 90D, 95D,99D,99.9D);

}
