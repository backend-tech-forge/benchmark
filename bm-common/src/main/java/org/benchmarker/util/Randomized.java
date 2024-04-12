package org.benchmarker.util;

/**
 * For randomized object generation
 *
 * @param <T>
 */
public interface Randomized<T> {

    /**
     * Generate randomized object
     *
     * @return T
     */
    T random();
}
