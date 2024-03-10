package org.benchmarker.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.template.model.TestTemplate;
import org.benchmarker.template.repository.TestTemplateRepository;
import org.benchmarker.template.service.TestTemplateService;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.util.annotations.RestDocsTest;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RestDocsTest
class TestTemplateApiControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestTemplateService testTemplateService;

    @MockBean
    private TestTemplateRepository testTemplateRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    UserGroupRepository userGroupRepository;

    @AfterEach
    void removeAll() {
        testTemplateRepository.deleteAll();
    }

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("탬플릿 생성 호출하는 테스트")
    public void createTemplate() throws Exception {

        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        TestTemplate testTemplate = request.toEntity();

        // when
        when(testTemplateService.createTemplate(any())).thenReturn(Optional.of(testTemplate.convertToResponseDto()));

        // then
        mockMvc.perform(post("/api/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    TestTemplateResponseDto resTemplate = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            TestTemplateResponseDto.class);

                    assertThat(resTemplate.getUrl()).isEqualTo(request.getUrl());
                    assertThat(resTemplate.getMethod()).isEqualTo(request.getMethod());
                    assertThat(resTemplate.getBody()).isEqualTo(request.getBody());
                    assertThat(resTemplate.getUserGroupName()).isEqualTo(userGroup.getName());
                    assertThat(resTemplate.getVuser()).isEqualTo(request.getVuser());
                    assertThat(resTemplate.getCpuLimit()).isEqualTo(request.getCpuLimit());
                    assertThat(resTemplate.getMaxRequest()).isEqualTo(request.getMaxRequest());
                    assertThat(resTemplate.getMaxDuration()).isEqualTo(request.getMaxDuration());
                });
    }

    @Test
    @DisplayName("존재 하지 않는 그룹과 함께 템플릿 생성 호출할 경우 에러 처리")
    public void createUserGroupException() throws Exception {
        //given

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("notGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(4)
                .maxDuration(3)
                .build();

        TestTemplate testTemplate = request.toEntity();

        // when
        when(testTemplateService.createTemplate(any())).thenThrow(new GlobalException(ErrorCode.GROUP_NOT_FOUND));

        // then
        mockMvc.perform(post("/api/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GROUP_NOT_FOUND.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.GROUP_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.GROUP_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("템플릿 조회 테스트")
    public void getTemplate() throws Exception {
        // given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        UserGroup tempGroup = userGroupRepository.save(userGroup);

        TestTemplateResponseDto resTestTemplate = TestTemplateResponseDto.builder()
                .id(10)
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        // when
        when(testTemplateService.getTemplate(any())).thenReturn(resTestTemplate);

        // then
        mockMvc.perform(get("/api/template/{template_id}", resTestTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    TestTemplateResponseDto resTemplate = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            TestTemplateResponseDto.class);

                    assertThat(resTemplate.getUrl()).isEqualTo(resTestTemplate.getUrl());
                    assertThat(resTemplate.getMethod()).isEqualTo(resTestTemplate.getMethod());
                    assertThat(resTemplate.getBody()).isEqualTo(resTestTemplate.getBody());
                    assertThat(resTemplate.getUserGroupName()).isEqualTo(userGroup.getName());
                    assertThat(resTemplate.getVuser()).isEqualTo(resTestTemplate.getVuser());
                    assertThat(resTemplate.getCpuLimit()).isEqualTo(resTestTemplate.getCpuLimit());
                    assertThat(resTemplate.getMaxRequest()).isEqualTo(resTestTemplate.getMaxRequest());
                    assertThat(resTemplate.getMaxDuration()).isEqualTo(resTestTemplate.getMaxDuration());
                });
    }

    @Test
    @DisplayName("템플릿 업데이트 테스트")
    public void updateTemplate() throws Exception {
        //given
        TestTemplateUpdateDto reqTestTemplate = TestTemplateUpdateDto.builder()
                .id(10)
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        TestTemplateResponseDto resTestTemplate = TestTemplateResponseDto.builder()
                .id(10)
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        // when
        when(testTemplateService.updateTemplate(any())).thenReturn(Optional.of(resTestTemplate));

        // then
        mockMvc.perform(patch("/api/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqTestTemplate)))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    TestTemplateResponseDto resTemplate = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            TestTemplateResponseDto.class);

                    assertThat(resTemplate.getUrl()).isEqualTo(resTestTemplate.getUrl());
                    assertThat(resTemplate.getMethod()).isEqualTo(resTestTemplate.getMethod());
                    assertThat(resTemplate.getBody()).isEqualTo(resTestTemplate.getBody());
                    assertThat(resTemplate.getVuser()).isEqualTo(resTestTemplate.getVuser());
                    assertThat(resTemplate.getCpuLimit()).isEqualTo(resTestTemplate.getCpuLimit());
                    assertThat(resTemplate.getMaxRequest()).isEqualTo(resTestTemplate.getMaxRequest());
                    assertThat(resTemplate.getMaxDuration()).isEqualTo(resTestTemplate.getMaxDuration());
                });
    }

    @Test
    @DisplayName("템플릿 삭제 테스트")
    public void deleteTemplate() throws Exception {

        // when
        doNothing().when(testTemplateService).deleteTemplate(any());

        // then
        mockMvc.perform(delete("/api/template/{template_id}", 10))
                .andDo(result -> {
                })
                .andDo(print())
                .andExpect(status().is(200));
    }
}