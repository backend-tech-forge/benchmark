package org.benchmarker.bmcontroller.home;

import static java.lang.Thread.sleep;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthCheckController {
    // test endpoint & health check
    @GetMapping("/health")
    public String healthChecker() throws InterruptedException {
        sleep(100);
        return "success";
    }

}
