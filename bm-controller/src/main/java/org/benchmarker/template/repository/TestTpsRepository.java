package org.benchmarker.template.repository;

import org.benchmarker.template.model.TestTps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTpsRepository extends JpaRepository<TestTps, Integer> {
}
