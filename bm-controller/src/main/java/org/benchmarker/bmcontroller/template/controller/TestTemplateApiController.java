package org.benchmarker.bmcontroller.template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.bmcontroller.template.service.ITestTemplateService;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestTemplateApiController {

    private final ITestTemplateService testTemplateService;
    private final UserContext userContext;

    @PostMapping("/template")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<TestTemplateResponseDto> createTemplate(@RequestBody TestTemplateRequestDto reqTestTemplate) {
        return ResponseEntity.ok(testTemplateService.createTemplate(reqTestTemplate).get());
    }

    @GetMapping("/template/{template_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<TestTemplateResponseDto> getTemplate(@PathVariable Integer template_id) {
        return ResponseEntity.ok(testTemplateService.getTemplate(template_id));
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<TestTemplateResponseDto>> getTemplates() {
        return ResponseEntity.ok(testTemplateService.getAllTemplatesAdmin());
    }

    @GetMapping("/groups/{group_id}/templates")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<TestTemplateResponseDto>> getTemplates(@PathVariable("group_id") String group_id) {
        User currentUser = userContext.getCurrentUser();
        if (currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(testTemplateService.getTemplates(group_id));
        }
        return ResponseEntity.ok(testTemplateService.getTemplates(group_id, currentUser.getId()));
    }

    @PatchMapping("/template")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<TestTemplateResponseDto> updateTemplate(@RequestBody TestTemplateUpdateDto resTestTemplate) throws Exception {
        return ResponseEntity.ok(testTemplateService.updateTemplate(resTestTemplate).get());
    }

    @DeleteMapping("/template/{template_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer template_id) {
        testTemplateService.deleteTemplate(template_id);
        return ResponseEntity.ok().build();
    }

}
