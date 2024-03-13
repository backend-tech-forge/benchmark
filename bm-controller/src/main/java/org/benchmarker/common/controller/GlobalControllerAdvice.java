package org.benchmarker.common.controller;


import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.benchmarker.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.common.error.GlobalErrorResponse;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.common.model.Contact;
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
@Getter
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @Value("${benchmark.version}")
    private String version;
    @Value("${benchmark.name}")
    private String name;
    @Value("${benchmark.description}")
    private String description;

    @Value("${benchmark.contacts}")
    private List<String> contactStrings = new ArrayList<>();

    private List<Contact> contacts;
    private final UserContext userContext;

    @PostConstruct
    private void initialize() {
        this.contacts = initializeContacts();
    }

    private List<Contact> initializeContacts() {
        List<Contact> contacts = new ArrayList<>();
        for (String contactString : contactStrings) {
            String[] parts = contactString.split(";");
            if (parts.length == 2) {
                contacts.add(new Contact(parts[0], parts[1]));
            }
        }
        System.out.print("contacts: " + contacts);
        return contacts;
    }
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalErrorResponse> handleGlobalException(GlobalException e) {
        return GlobalErrorResponse.toResponseEntity(e);
    }

    @ModelAttribute
    public void globalAttributes(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("projectName", name);
        model.addAttribute("projectDesc", description);
        model.addAttribute("contributor", contacts);
        try {
            model.addAttribute("currentUser", userContext.getCurrentUser());
        } catch (Exception e) {
            noOp();
        }
    }
}
