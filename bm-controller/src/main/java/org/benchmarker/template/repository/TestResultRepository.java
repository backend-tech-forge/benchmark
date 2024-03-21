package org.benchmarker.template.repository;

import org.benchmarker.template.model.TestResult;
import org.benchmarker.template.model.TestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Integer> {

    TestResult findByTestTemplate(TestTemplate testTemplate);
}
