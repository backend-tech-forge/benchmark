package org.benchmarker.bmcontroller.template.repository;

import java.util.UUID;
import org.benchmarker.bmcontroller.template.model.TestExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, UUID> {

    @Query("select te from TestExecution te where te.testTemplate.id = :templateId order by te.createdAt desc")
    Page<TestExecution> findAllByTestTemplateId(@Param("templateId") Integer templateId, Pageable pageable);

}