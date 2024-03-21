package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TestMttfb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMttfbRepository extends JpaRepository<TestMttfb, Integer> {
}
