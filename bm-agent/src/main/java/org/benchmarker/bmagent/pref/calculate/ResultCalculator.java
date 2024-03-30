package org.benchmarker.bmagent.pref.calculate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResultCalculator implements IResultCalculator {

    @SafeVarargs
    @Override
    public final <T extends Number & Comparable<T>> Double average(T... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided.");
        }

        // calculate values average
        double sum = values[0].doubleValue();
        for (int i = 1; i < values.length; i++) {
            sum += values[i].doubleValue();
        }
        double average = sum / values.length;
        return Double.valueOf(average);
    }

    @Override
    public <T extends Number & Comparable<T>> Map<Double, T> percentile(
        Map<LocalDateTime, T> results, List<Double> percentile, Boolean reverse) {

        Map<LocalDateTime, T> snapshot = new ConcurrentHashMap<>(results);
        List<T> values;
        if (!reverse) {
            values = snapshot.values().stream().sorted(Comparator.reverseOrder()).toList();
        } else {
            values = snapshot.values().stream().sorted().toList();
        }

        Map<Double, T> result = new HashMap<>();
        int size = values.size();
        for (Double p : percentile) {
            int index = (int) Math.ceil((p / 100) * size) - 1;
            if (index < 0) {
                result.put(p, values.get(0));
            } else {
                result.put(p, values.get(index));
            }
        }

        return result;
    }
}
