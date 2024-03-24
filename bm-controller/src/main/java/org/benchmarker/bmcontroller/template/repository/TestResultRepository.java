package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TestResult;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Integer> {

    List<TestResult> findByTestTemplate(TestTemplate testTemplate);
}
