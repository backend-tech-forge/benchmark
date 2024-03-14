package org.benchmarker.template.repository;

import org.benchmarker.template.model.TestErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestErrorLogRepository extends JpaRepository<TestErrorLog, Integer> {
}
