package org.benchmarker.bmcontroller.home;

import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class HealthCheckControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private HealthCheckController healthCheckController;
    @MockBean
    private UserContext userContext;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(healthCheckController).build();
    }

    @Test
    void testHealthChecker() throws Exception {
        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/health"))
            // then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("success"));
    }
}