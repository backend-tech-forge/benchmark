package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.Tps;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TpsRepository extends JpaRepository<Tps, Integer> {

    List<Tps> findByTestResult(TestResult testResult);
}
