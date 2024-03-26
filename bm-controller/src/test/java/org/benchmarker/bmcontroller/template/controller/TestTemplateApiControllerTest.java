package org.benchmarker.bmcontroller.template.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.common.util.JsonMapper;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.service.TestTemplateService;
import org.benchmarker.bmcontroller.user.controller.constant.TestUserConsts;
import org.benchmarker.bmcontroller.user.helper.UserHelper;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.benchmarker.bmcontroller.user.service.UserContext;
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
    private JsonMapper jsonMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
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
        when(jsonMapper.isValidJson(any())).thenReturn(true);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }


    @Test
    @DisplayName("탬플릿 생성 호출하는 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void createTemplate() throws Exception {

        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("userGroup")
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
                    assertThat(resTemplate.getUserGroupId()).isEqualTo(userGroup.getName());
                    assertThat(resTemplate.getVuser()).isEqualTo(request.getVuser());
                    assertThat(resTemplate.getCpuLimit()).isEqualTo(request.getCpuLimit());
                    assertThat(resTemplate.getMaxRequest()).isEqualTo(request.getMaxRequest());
                    assertThat(resTemplate.getMaxDuration()).isEqualTo(request.getMaxDuration());
                });
    }

    @Test
    @DisplayName("존재 하지 않는 그룹과 함께 템플릿 생성 호출할 경우 에러 처리")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void createUserGroupException() throws Exception {

        //given
        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("notGroup")
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
    @DisplayName("필수 값을 넣지 않고 템플릿 생성 호출할 경우 에러 처리")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void createTemplateWithoutRequiredFieldsThrowsException() throws Exception {

        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
//                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        TestTemplate testTemplate = request.toEntity();

        // when
        when(testTemplateService.createTemplate(any())).thenThrow(new GlobalException(ErrorCode.BAD_REQUEST));

        // then
        mockMvc.perform(post("/api/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.BAD_REQUEST.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BAD_REQUEST.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.BAD_REQUEST.name()));
    }

    @Test
    @DisplayName("템플릿 조회 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void getTemplate() throws Exception {
        // given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        UserGroup tempGroup = userGroupRepository.save(userGroup);

        TestTemplateResponseDto resTestTemplate = TestTemplateResponseDto.builder()
                .id(10)
                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("userGroup")
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
                    assertThat(resTemplate.getUserGroupId()).isEqualTo(userGroup.getName());
                    assertThat(resTemplate.getVuser()).isEqualTo(resTestTemplate.getVuser());
                    assertThat(resTemplate.getCpuLimit()).isEqualTo(resTestTemplate.getCpuLimit());
                    assertThat(resTemplate.getMaxRequest()).isEqualTo(resTestTemplate.getMaxRequest());
                    assertThat(resTemplate.getMaxDuration()).isEqualTo(resTestTemplate.getMaxDuration());
                });
    }

    @Test
    @DisplayName("그룹이 가지는 템플릿 리스트 조회 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void getTemplateWithGroup() throws Exception {
        // given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        User user = UserHelper.createDefaultUser();
        UserGroupJoin userGroupJoin = UserGroupJoin.builder().user(user).userGroup(userGroup).build();

        UserGroup tempGroup = userGroupRepository.save(userGroup);
        User tempUser = userRepository.save(user);
        UserGroupJoin tempJoin = userGroupJoinRepository.save(userGroupJoin);

        TestTemplateResponseDto resTestTemplate = TestTemplateResponseDto.builder()
            .id(10)
            .url("test.com")
            .method("get")
            .body("")
            .userGroupId("userGroup")
            .vuser(3)
            .cpuLimit(3)
            .maxRequest(3)
            .maxDuration(3)
            .build();

        // when
        when(testTemplateService.getTemplates(userGroup.getId(),user.getId())).thenReturn(List.of(resTestTemplate));
        when(userContext.getCurrentUser()).thenReturn(user);

        // then
        mockMvc.perform(get("/api/groups/{group_id}/templates", userGroup.getId()))
            .andDo(print())
            .andDo(result -> {
                assertThat(result.getResponse().getStatus()).isEqualTo(200);

                String jsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

                TypeFactory typeFactory = objectMapper.getTypeFactory();
                CollectionType listType = typeFactory.constructCollectionType(List.class, TestTemplateResponseDto.class);
                List<TestTemplateResponseDto> responseList = objectMapper.readValue(jsonResponse, listType);


                assertThat(responseList.get(0).getUrl()).isEqualTo(resTestTemplate.getUrl());
                assertThat(responseList.get(0).getMethod()).isEqualTo(resTestTemplate.getMethod());
                assertThat(responseList.get(0).getBody()).isEqualTo(resTestTemplate.getBody());
                assertThat(responseList.get(0).getUserGroupId()).isEqualTo(userGroup.getName());
                assertThat(responseList.get(0).getVuser()).isEqualTo(resTestTemplate.getVuser());
                assertThat(responseList.get(0).getCpuLimit()).isEqualTo(resTestTemplate.getCpuLimit());
                assertThat(responseList.get(0).getMaxRequest()).isEqualTo(resTestTemplate.getMaxRequest());
                assertThat(responseList.get(0).getMaxDuration()).isEqualTo(resTestTemplate.getMaxDuration());
            });
    }

    @Test
    @DisplayName("템플릿 업데이트 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void updateTemplate() throws Exception {
        //given
        TestTemplateUpdateDto reqTestTemplate = TestTemplateUpdateDto.builder()
                .id(10)
                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("userGroup")
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
                .userGroupId("userGroup")
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
    @DisplayName("템플릿 업데이트시 필수 정보 없이 호출 하면 에러 발생하기")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void updateTemplateWithoutRequiredFieldsThrowsException() throws Exception {
        //given
        TestTemplateUpdateDto reqTestTemplate = TestTemplateUpdateDto.builder()
                .id(10)
//                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        // when
        when(testTemplateService.updateTemplate(any())).thenThrow(new GlobalException(ErrorCode.BAD_REQUEST));

        // then
        mockMvc.perform(patch("/api/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqTestTemplate)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.BAD_REQUEST.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BAD_REQUEST.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.BAD_REQUEST.name()));
    }

    @Test
    @DisplayName("템플릿 삭제 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
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