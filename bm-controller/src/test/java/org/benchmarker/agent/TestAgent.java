package org.benchmarker.agent;

import static org.assertj.core.api.Assertions.assertThat;

import org.benchmark.bmagent.pref.ResultManagerService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ResultManagerService.class})
public class TestAgent {
    @Autowired
    private ResultManagerService resultManagerService;

    @Test
    public void test() {
        resultManagerService.getResult(1L);

        assertThat(resultManagerService.getResult(1L)).isNull();
    }
}
