package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestStatusRepository extends JpaRepository<TestStatus, Integer> {
}
