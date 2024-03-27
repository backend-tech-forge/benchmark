package org.benchmarker.bmcontroller.template.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.service.TestResultService;
import org.benchmarker.bmcontroller.template.service.TestTemplateService;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.benchmarker.bmcontroller.user.service.GroupService;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.util.annotations.RestDocsTest;

@SpringBootTest
@RestDocsTest
class TestResultTemplateApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestTemplateService testTemplateService;

    @MockBean
    private TestResultService testResultService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TestTemplateRepository testTemplateRepository;
    @Autowired
    private TestResultRepository testResultRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @MockBean
    UserContext userContext;

    @MockBean
    UserGroupJoinRepository userGroupJoinRepository;

    @AfterEach
    void removeAll() {
        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();
        userGroupJoinRepository.deleteAll();
        userGroupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    public Optional<TestTemplateResponseDto> saveGetTempData() throws JsonProcessingException {

        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();

        UserGroup saveUserGroup = userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("http://localhost:8080/login")
                .method("get")
                .body("")
                .userGroupId(saveUserGroup.getId())
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        return testTemplateService.createTemplate(request);
    }

}