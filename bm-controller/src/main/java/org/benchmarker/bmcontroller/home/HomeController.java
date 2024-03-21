package org.benchmarker.bmcontroller.home;

import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
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