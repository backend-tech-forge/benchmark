package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;

import java.util.List;
import java.util.Optional;

import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;

public interface ITestResultService {

    Optional<CommonTestResult> resultSaveAndReturn(CommonTestResult commonTestResult);

    TestTemplateResponseDto getTemplateResult(Integer templateResultId);

    List<TestResultResponseDto> getGroupTemplateResult(String groupId);

}
