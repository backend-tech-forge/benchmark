package org.benchmarker.bmcontroller.template.repository;

import java.util.UUID;
import org.benchmarker.bmcontroller.template.model.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, UUID> {

}