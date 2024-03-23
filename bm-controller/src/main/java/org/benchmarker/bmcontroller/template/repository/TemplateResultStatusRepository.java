package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TemplateResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateResultStatusRepository extends JpaRepository<TemplateResultStatus, Integer> {
}
