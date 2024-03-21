package org.benchmarker.template.repository;

import org.benchmarker.template.model.TestResult;
import org.benchmarker.template.model.TestTps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestTpsRepository extends JpaRepository<TestTps, Integer> {

    List<TestTps> findByTestResult(TestResult testResult);
}
