package org.benchmarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BmControllerApplication {

    public static void main(String[] args) {
        System.setProperty("spring.application.name","bm-controller");
        SpringApplication.run(BmControllerApplication.class, args);
    }

}
