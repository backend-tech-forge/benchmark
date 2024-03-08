package org.benchmarker.template.service;

import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.template.model.TestTemplate;

import java.util.List;
import java.util.Optional;

public interface ITestTemplateService {

    Optional<TestTemplate> createTemplate(TestTemplateRequestDto testTemplate);

    TestTemplate getTemplate(Integer id);

    List<TestTemplate> getTemplates();

    Optional<TestTemplate> updateTemplate(TestTemplateUpdateDto testTemplate) throws Exception;

    void deleteTemplate(Integer id);
}
