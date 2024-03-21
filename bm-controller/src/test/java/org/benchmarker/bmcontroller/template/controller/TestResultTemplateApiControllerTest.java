package org.benchmarker.bmcontroller.template.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

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

//    @Test
//    @DisplayName("탬플릿 결과 호출하는 테스트")
//    @WithMockUser(username = TestUserConsts.id, roles = "USER")
//    public void createTemplateResult() throws Exception {
//
//        //given
//        User defaultUser = UserHelper.createDefaultUser();
//        TestTemplateResponseDto testTemplateResponseDto = saveGetTempData()
//                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));
//
//        TestResultResponseDto request = TestResultResponseDto.builder()
//                .url(testTemplateResponseDto.getUrl())
//                .method(testTemplateResponseDto.getMethod())
//                .totalSuccess(3)
//                .totalRequest(3)
//                .totalError(0)
//                .build();
//
//        // when
//        when(userContext.getCurrentUser()).thenReturn(defaultUser);
//        when(testResultService.measurePerformance("userGroup", testTemplateResponseDto.getId(), "start")).thenReturn(request);
//
//        // then
//        mockMvc.perform(post("/api/groups/{group_id}/templates/{template_id}?action={action}"
//                                , "userGroup", testTemplateResponseDto.getId(), "start")
//                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(request))
//                )
//                .andDo(print())
//                .andDo(result -> {
//                    assertThat(result.getResponse().getStatus()).isEqualTo(200);
//
//                    TestResultResponseDto resTemplate = objectMapper.readValue(
//                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
//                            TestResultResponseDto.class);
//
//                    assertThat(resTemplate.getUrl()).isEqualTo(request.getUrl());
//                    assertThat(resTemplate.getMethod()).isEqualTo(request.getMethod());
//                    assertThat(resTemplate.getTotalRequest()).isEqualTo(request.getTotalRequest());
//                    assertThat(resTemplate.getTotalSuccess()).isEqualTo(request.getTotalSuccess());
//                    assertThat(resTemplate.getTotalError()).isEqualTo(request.getTotalError());
//
//                });
//    }

    public Optional<TestTemplateResponseDto> saveGetTempData() {

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