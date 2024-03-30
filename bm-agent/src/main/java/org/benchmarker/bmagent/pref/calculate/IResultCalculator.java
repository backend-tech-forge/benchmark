package org.benchmarker.bmagent.pref.calculate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * This interface support useful methods for calculating average, percentile.
 *
 * @author gyumin hwangbo
 * @since 2024-03-30
 */
public interface IResultCalculator {

    /**
     * Calculates the average of a set of values.
     *
     * @param values The values to calculate the average of.
     * @param T      The type of values. Must extend Number and implement Comparable.
     * @return The average of the provided values as a Double.
     * @throws IllegalArgumentException if no values are provided.
     */
    <T extends Number & Comparable<T>> Double average(T... values);


    /**
     * Calculates percentiles from a map of results.
     *
     * @param results    The map containing the results with their associated date and time.
     * @param percentile The list of percentiles to calculate.
     * @param reverse    Determines whether to calculate percentiles in reverse order.
     * @param T          The type of values in the map. Must extend Number and implement
     *                   Comparable.
     * @return A map containing the calculated percentiles along with their associated values.
     */
    <T extends Number & Comparable<T>> Map<Double, T> percentile(Map<LocalDateTime, T> results,
        List<Double> percentile, Boolean reverse);
}
