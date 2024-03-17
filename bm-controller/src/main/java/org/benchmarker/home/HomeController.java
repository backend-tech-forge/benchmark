package org.benchmarker.home;

import org.benchmarker.common.controller.annotation.GlobalControllerModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@GlobalControllerModel
public class HomeController {

    @GetMapping({"/home", "/"})
    public String home() {
        return "home";
    }
}