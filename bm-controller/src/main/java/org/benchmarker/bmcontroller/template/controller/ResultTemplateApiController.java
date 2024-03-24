package org.benchmarker.bmcontroller.template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultReqDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;
import org.benchmarker.bmcontroller.template.service.ITestResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResultTemplateApiController {

    private final ITestResultService testResultService;

    @PostMapping("/testResult")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<SaveResultResDto> measurePerformance(@RequestBody SaveResultReqDto request) {
        return ResponseEntity.ok(testResultService.resultSaveAndReturn(request).get());
    }

}
