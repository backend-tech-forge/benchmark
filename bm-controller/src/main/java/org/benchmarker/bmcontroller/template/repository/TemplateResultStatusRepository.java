package org.benchmarker.bmcontroller.template.repository;

import org.benchmarker.bmcontroller.template.model.Mttfb;
import org.benchmarker.bmcontroller.template.model.TemplateResultStatus;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateResultStatusRepository extends JpaRepository<TemplateResultStatus, Integer> {

    List<TemplateResultStatus> findByTestResult(TestResult testResult);
}
