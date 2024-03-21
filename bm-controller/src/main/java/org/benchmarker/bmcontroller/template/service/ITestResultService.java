package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;

import java.util.List;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;

public interface ITestResultService {

    TestResultResponseDto measurePerformance(String group_id, Integer templateId, String action) throws InterruptedException;

    TestTemplateResponseDto getTemplateResult(Integer templateResultId);

    List<TestResultResponseDto> getGroupTemplateResult(String groupId);

}
