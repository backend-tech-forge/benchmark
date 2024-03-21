package org.benchmarker.bmagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestBmAgentApplication {

    public static void main(String[] args) {
        SpringApplication.from(BmAgentApplication::main).with(TestBmAgentApplication.class).run(args);
    }

}
