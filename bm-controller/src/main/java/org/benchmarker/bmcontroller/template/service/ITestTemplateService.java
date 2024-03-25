package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ITestTemplateService {

    Optional<TestTemplateResponseDto> createTemplate(TestTemplateRequestDto testTemplate);

    TestTemplateResponseDto getTemplate(Integer id);

    List<TestTemplateResponseDto> getAllTemplatesAdmin();

    List<TestTemplateResponseDto> getTemplates(String groupId, String userId);

    List<TestTemplateResponseDto> getTemplates(String groupId);

    Optional<TestTemplateResponseDto> updateTemplate(TestTemplateUpdateDto testTemplate, String userId);

    void deleteTemplate(Integer id, String userId);
}
