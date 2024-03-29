package org.benchmarker.template.service;

import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ITestTemplateService {

    Optional<TestTemplateResponseDto> createTemplate(TestTemplateRequestDto testTemplate);

    TestTemplateResponseDto getTemplate(Integer id);

    List<TestTemplateResponseDto> getAllTemplatesAdmin();
    public List<TestTemplateResponseDto> getTemplates(String groupId, String userId);
    public List<TestTemplateResponseDto> getTemplates(String groupId);
    Optional<TestTemplateResponseDto> updateTemplate(TestTemplateUpdateDto testTemplate) throws Exception;

    void deleteTemplate(Integer id);
}
