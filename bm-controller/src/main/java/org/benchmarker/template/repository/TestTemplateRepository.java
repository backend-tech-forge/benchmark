package org.benchmarker.template.repository;

import java.util.List;
import org.benchmarker.template.model.TestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTemplateRepository extends JpaRepository<TestTemplate, Integer> {

    List<TestTemplate> findAllByUserGroupId(String userGroupId);
}
