package org.benchmarker.common.controller;


import lombok.RequiredArgsConstructor;
import org.benchmarker.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.common.error.GlobalErrorResponse;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.service.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import static org.benchmarker.common.util.NoOp.noOp;

/**
 * All controllers should have these common model attributes.
 */
@ControllerAdvice(annotations = GlobalControllerModel.class)
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @Value("${benchmark.version}")
    private String version;
    @Value("${benchmark.name}")
    private String name;
    @Value("${benchmark.description}")
    private String description;
    @Value("${benchmark.contact.name}")
    private String contactName;
    @Value("${benchmark.contact.email}")
    private String contactEmail;

    private final UserContext userContext;

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalErrorResponse> handleGlobalException(GlobalException e) {
        return GlobalErrorResponse.toResponseEntity(e);
    }
    @ModelAttribute
    public void globalAttributes(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("projectName", name);
        model.addAttribute("projectDesc", description);
        model.addAttribute("contributorName", contactName);
        model.addAttribute("contributorEmail", contactEmail);
        try {
            model.addAttribute("currentUser", userContext.getCurrentUser());
        } catch (Exception e) {
            noOp();
        }
    }
}
