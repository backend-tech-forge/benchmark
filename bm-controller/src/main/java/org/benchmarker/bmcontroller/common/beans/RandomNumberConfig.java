package org.benchmarker.bmcontroller.common.beans;

import org.benchmarker.bmcontroller.mail.common.strategy.IRandomCodeGenerator;
import org.benchmarker.bmcontroller.mail.common.strategy.RandomCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RandomNumberConfig {
    @Bean
    public IRandomCodeGenerator randomNumber() {
        return new RandomCodeGenerator();
    }

}
