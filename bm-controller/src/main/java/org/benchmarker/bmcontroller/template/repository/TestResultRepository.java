package org.benchmarker.bmcontroller.template.repository;

import java.util.List;
import java.util.UUID;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Integer> {

    @Query("select tr from TestResult tr where tr.testExecution.testTemplate.id = :templateId")
    TestResult findByTestTemplate(@Param("templateId") Integer templateId);

    @Query("select tr from TestResult tr where tr.testExecution.id = :testExecutionId order by tr.createdAt desc")
    List<TestResult> findAllByTestExecutionId(@Param("testExecutionId") UUID testExecutionId);
}
