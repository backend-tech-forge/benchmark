package org.benchmarker.bmcontroller.template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;
import org.benchmarker.bmcontroller.template.service.ITestResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestResultTemplateApiController {

    private final ITestResultService testResultService;

    @PostMapping("/groups/{group_id}/templates/{template_id}?action={action}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<TestResultResponseDto> measurePerformance(
            @PathVariable String  group_id,
            @PathVariable Integer template_id,
            @RequestParam(required = true) String action) throws InterruptedException {
        return ResponseEntity.ok(testResultService.measurePerformance(group_id, template_id, action));
    }

}
