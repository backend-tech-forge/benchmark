package org.benchmarker.template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.template.service.ITestResultService;
import org.benchmarker.template.service.ITestTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestResultTemplateApiController {

    private final ITestResultService testResultService;

//    @PostMapping("/benchmark")
//    @PreAuthorize("hasAnyRole('USER')")
//    public ResponseEntity<TestTemplateResponseDto> measurePerformance(@RequestBody TestTemplateRequestDto reqTestTemplate) {
//        return ResponseEntity.ok(testTemplateService.createTemplate(reqTestTemplate).get());
//    }
//
//    @GetMapping("/benchmark/{benchmark}")
//    @PreAuthorize("hasAnyRole('USER')")
//    public ResponseEntity<TestTemplateResponseDto> getBenchmark(@PathVariable Integer template_id) {
//        return ResponseEntity.ok(testTemplateService.getTemplate(template_id));
//    }
//
//    @GetMapping("/benchmark")
//    @PreAuthorize("hasAnyRole('USER')")
//    public ResponseEntity<List<TestTemplateResponseDto>> getBenchmark() {
//        return ResponseEntity.ok(testTemplateService.getTemplates());
//    }

    @GetMapping("hello")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String hello() throws InterruptedException {
        testResultService.measurePerformance(3);
        return "hello";
    }

    @GetMapping("world")
    public String world() throws InterruptedException {
        return "world";
    }
}
