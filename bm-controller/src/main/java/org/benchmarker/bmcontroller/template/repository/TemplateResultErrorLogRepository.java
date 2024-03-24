package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.TemplateResultErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateResultErrorLogRepository extends JpaRepository<TemplateResultErrorLog, Integer> {
}
