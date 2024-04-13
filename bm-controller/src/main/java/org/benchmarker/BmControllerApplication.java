package org.benchmarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BmControllerApplication {

    public static void main(String[] args) {
        System.setProperty("spring.application.name","bm-controller");
        SpringApplication.run(BmControllerApplication.class, args);
    }

}
