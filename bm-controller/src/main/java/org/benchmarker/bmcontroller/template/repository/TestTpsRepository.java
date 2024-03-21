package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TestTps;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestTpsRepository extends JpaRepository<TestTps, Integer> {

    List<TestTps> findByTestResult(TestResult testResult);
}
