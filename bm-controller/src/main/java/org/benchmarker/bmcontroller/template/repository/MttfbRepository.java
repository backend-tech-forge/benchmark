package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.Mttfb;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MttfbRepository extends JpaRepository<Mttfb, Integer> {

    List<Mttfb> findByTestResult(TestResult testResult);
}
