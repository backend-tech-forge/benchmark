package org.benchmarker.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.template.controller.dto.TestResultResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.repository.TestTemplateRepository;
import org.benchmarker.template.service.TestResultService;
import org.benchmarker.template.service.TestTemplateService;
import org.benchmarker.user.controller.constant.TestUserConsts;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.repository.UserGroupJoinRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.benchmarker.user.service.GroupService;
import org.benchmarker.user.service.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.util.annotations.RestDocsTest;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@RestDocsTest
class TestResultTemplateApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestTemplateService testTemplateService;

    @Autowired
    private TestResultService testResultService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TestTemplateRepository testTemplateRepository;

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

    @Test
    @DisplayName("탬플릿 결과 호출하는 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void createTemplateResult() throws Exception {

        //given
        TestTemplateResponseDto testTemplateResponseDto = saveGetTempData()
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        TestResultResponseDto request = TestResultResponseDto.builder()
                .url(testTemplateResponseDto.getUrl())
                .method(testTemplateResponseDto.getMethod())
                .totalSuccess(3)
                .totalRequest(3)
                .totalError(0)
                .build();

        // when
        when(testResultService.measurePerformance("userGroup", testTemplateResponseDto.getId(), "START")).thenReturn(request);

        // then
        mockMvc.perform(post("/api/groups/{group_id}/templates/{template_id}?action={action}"
                                , "userGroup", testTemplateResponseDto.getId(), "START")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    TestResultResponseDto resTemplate = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            TestResultResponseDto.class);

                    assertThat(resTemplate.getUrl()).isEqualTo(request.getUrl());
                    assertThat(resTemplate.getMethod()).isEqualTo(request.getMethod());
                    assertThat(resTemplate.getTotalRequest()).isEqualTo(request.getTotalRequest());
                    assertThat(resTemplate.getTotalSuccess()).isEqualTo(request.getTotalSuccess());
                    assertThat(resTemplate.getTotalError()).isEqualTo(request.getTotalError());

                });
    }

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