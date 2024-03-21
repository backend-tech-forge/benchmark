package org.benchmarker.bmagent.service;

public interface MapManager<K, V> {

    /**
     * Find Object from map
     * <p>if cannot find object, return null</p>
     *
     * @param id {@link K}
     * @return {@link V} or null
     */
    V find(K id);

    /**
     * Save Object to map
     *
     * @param id {@link K}
     * @param object
     */
    void save(K id, V object);

    /**
     * Remove Object from map
     *
     * @param id
     */
    void remove(K id);

}
