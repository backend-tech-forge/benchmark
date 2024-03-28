package org.benchmarker.bmagent;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BmAgentApplication {
    @Bean
    public ExitCodeGenerator exitCodeGenerator() {
        return new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                return 42;
            }
        };
    }

    public static void main(String[] args) {
        System.setProperty("spring.application.name","bm-agent");
        SpringApplication.run(BmAgentApplication.class, args);
    }
}
