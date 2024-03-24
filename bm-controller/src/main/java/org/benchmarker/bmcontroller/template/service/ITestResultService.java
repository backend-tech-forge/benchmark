package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcontroller.template.controller.dto.SaveResultReqDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;

import java.util.List;
import java.util.Optional;

public interface ITestResultService {

    Optional<SaveResultResDto> resultSaveAndReturn(SaveResultReqDto request);

    TestTemplateResponseDto getTemplateResult(Integer templateResultId);

    List<TestResultResponseDto> getGroupTemplateResult(String groupId);

}
