package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.Mttfb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MttfbRepository extends JpaRepository<Mttfb, Integer> {
}
