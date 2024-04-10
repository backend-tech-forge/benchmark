package org.benchmarker.bmcontroller.preftest.helper;

import org.benchmarker.util.Randomized;

/**
 * For testing
 */
public class TestHelper {

    public static <T extends Randomized<T>> T random(Class<T> clazz) {
        try {
            return clazz.newInstance().random();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
