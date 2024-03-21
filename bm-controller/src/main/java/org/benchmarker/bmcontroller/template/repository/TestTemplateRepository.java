package org.benchmarker.bmcontroller.template.repository;

import java.util.List;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTemplateRepository extends JpaRepository<TestTemplate, Integer> {

    List<TestTemplate> findAllByUserGroupId(String userGroupId);
}
