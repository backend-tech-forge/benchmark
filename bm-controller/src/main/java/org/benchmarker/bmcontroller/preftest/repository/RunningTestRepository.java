package org.benchmarker.bmcontroller.preftest.repository;

import org.benchmarker.bmcontroller.preftest.model.RunningTest;
import org.springframework.data.repository.CrudRepository;

public interface RunningTestRepository extends CrudRepository<RunningTest, Integer> {
}
