package org.benchmarker.template.repository;

import org.benchmarker.template.model.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestStatusRepository extends JpaRepository<TestStatus, Integer> {
}
