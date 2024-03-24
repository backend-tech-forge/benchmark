package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcontroller.template.controller.dto.ResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultReqDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;

import java.util.Optional;

public interface ITestResultService {

    Optional<SaveResultResDto> resultSaveAndReturn(SaveResultReqDto request);

    ResultResDto getTemplateResult(Integer templateResultId);

}
