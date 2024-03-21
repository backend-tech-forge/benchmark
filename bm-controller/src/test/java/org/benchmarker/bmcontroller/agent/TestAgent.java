package org.benchmarker.bmcontroller.agent;

import static org.assertj.core.api.Assertions.assertThat;

import org.benchmarker.bmagent.pref.ResultManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestAgent {
    private ResultManagerService resultManagerService;

    @BeforeEach
    public void setUp() {
        resultManagerService = new ResultManagerService();
    }

    @Test
    public void test() {
        resultManagerService.find(1L);
        assertThat(resultManagerService.find(1L)).isNull();
    }

}
