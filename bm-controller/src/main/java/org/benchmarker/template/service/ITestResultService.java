package org.benchmarker.template.service;

import org.benchmarker.template.controller.dto.TestResultResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;

import java.util.List;
import java.util.Optional;

public interface ITestResultService {

    TestResultResponseDto measurePerformance(Integer templateId) throws InterruptedException;

    TestTemplateResponseDto getTemplateResult(Integer templateResultId);

    List<TestResultResponseDto> getTemplates(String groupId);

}
