package org.benchmarker.template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.template.service.TestTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestTemplateApiController {

    private final TestTemplateService testTemplateService;

    @PostMapping("/template")
    public ResponseEntity<Optional<TestTemplateResponseDto>> createTemplate(@RequestBody TestTemplateRequestDto reqTestTemplate) {
        return ResponseEntity.ok(testTemplateService.createTemplate(reqTestTemplate));
    }

    @GetMapping("/template")
    public ResponseEntity<TestTemplateResponseDto> getTemplate(@RequestParam Integer id) {
        return ResponseEntity.ok(testTemplateService.getTemplate(id));
    }

    @GetMapping("/template")
    public ResponseEntity<List<TestTemplateResponseDto>> getTemplates() {
        return ResponseEntity.ok(testTemplateService.getTemplates());
    }

    @PatchMapping("/template")
    public ResponseEntity<Optional<TestTemplateResponseDto>> updateTemplate(@RequestBody TestTemplateUpdateDto resTestTemplate) throws Exception {
        return ResponseEntity.ok(testTemplateService.updateTemplate(resTestTemplate));
    }

    @DeleteMapping("/template")
    public ResponseEntity<Void> deleteTemplate(@RequestParam Integer id) {
        testTemplateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

}
