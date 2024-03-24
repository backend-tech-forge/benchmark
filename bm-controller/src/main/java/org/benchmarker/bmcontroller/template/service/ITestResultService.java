package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcontroller.template.controller.dto.ResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;

import java.util.Optional;

public interface ITestResultService {

    Optional<SaveResultResDto> resultSaveAndReturn(CommonTestResult request);

    CommonTestResult getTemplateResult(Integer templateResultId);

}
