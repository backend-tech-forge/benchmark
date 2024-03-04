package org.benchmarker.user.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import org.benchmarker.common.controller.GlobalRestControllerAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.util.annotations.RestDocsTest;
import org.util.restdocs.RestDocsConsts;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@RestDocsTest
public class SpringSecurityTest {

    private RestDocumentationExtension restDocumentation = new RestDocumentationExtension ("custom");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void testSecuredEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(document(RestDocsConsts.GET_USERS));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void test01() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andDo(document(RestDocsConsts.GET_USERS));
    }
}
