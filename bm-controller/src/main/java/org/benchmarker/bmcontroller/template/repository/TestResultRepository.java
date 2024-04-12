package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Integer> {

    @Query("select tr from TestResult tr where tr.testExecution.testTemplate.id = :templateId")
    TestResult findByTestTemplate(@Param("templateId") Integer templateId);
}
