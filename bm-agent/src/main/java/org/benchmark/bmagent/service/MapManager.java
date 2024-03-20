package org.benchmark.bmagent.service;

import java.util.Optional;

public interface MapManager<K, V> {
    V getResult(K id);
    void addResult(K id, V result);

}
