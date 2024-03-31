package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TestMttfb;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestMttfbRepository extends JpaRepository<TestMttfb, Integer> {

    List<TestMttfb> findByTestResult(TestResult saveResult);
}
