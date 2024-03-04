package org.benchmarker.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.benchmarker.common.controller.GlobalRestControllerAdvice;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalErrorResponse;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.controller.constant.TestUserConsts;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.model.User;
import org.benchmarker.user.service.IUserService;
import org.benchmarker.user.service.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.util.annotations.RestDocsTest;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


//@ExtendWith({SpringExtension.class})
//@WebMvcTest(UserApiControllerTest.class)
@SpringBootTest
@AutoConfigureMockMvc
@RestDocsTest
class UserApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

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

    @Mock
    private IUserService userService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserApiController userApiController;

//    @BeforeEach
//    public void setUp() {
//        this.mockMvc = MockMvcBuilders
//            .standaloneSetup(userApiController)
//            .setControllerAdvice(new GlobalRestControllerAdvice())
//            .build();
//    }

    @Test
    @DisplayName("valid 유저 생성 시 200 OK 응답을 반환한다")
    void createUser_ReturnsOkResponse() throws Exception {
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .email(TestUserConsts.email)
            .build();
        User user = userRegisterDto.toEntity();

        // When
        when(userService.createUser(user)).thenReturn(Optional.ofNullable(user));

        // then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userRegisterDto)))
            .andDo(result -> {

                assertThat(result.getResponse().getStatus()).isEqualTo(200);
            });
    }

    @Test
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    @DisplayName("내 정보 조회 시 id 가 없다면 404 USER_NOT_FOUND 를 반환한다")
    void getUserWithNoId_ReturnsNotFound() throws Exception {
        // given
        String userId = TestUserConsts.id;

        // when
        when(userService.getUser(TestUserConsts.id, userId)).thenThrow(new GlobalException(ErrorCode.USER_NOT_FOUND));

        // then
        mockMvc.perform(get("/api/user")
                .param("id", userId))
            .andDo(result -> {
                assertThat(result.getResponse().getStatus()).isEqualTo(404);
                assertThat(
                    Objects.requireNonNull(result.getResolvedException()).getClass()).isEqualTo(
                    GlobalException.class);

                GlobalErrorResponse response = objectMapper.readValue(
                    result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                    GlobalErrorResponse.class);
                assertThat(response.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
                assertThat(response.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
            });
    }

    @Test
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    @DisplayName("USER 권한이 있는 유저 조회 시 403 FORBIDDEN 에러 반환")
    void getUsersWithUser_ReturnsListOfUserInfo() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
            .andDo(result -> {
                assertThat(result.getResponse().getStatus()).isEqualTo(403);

                GlobalErrorResponse ex = objectMapper.readValue(
                    result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                    GlobalErrorResponse.class);

                assertThat(ex.getCode()).isEqualTo(ErrorCode.FORBIDDEN.name());
                assertThat(ex.getMessage()).isEqualTo(ErrorCode.FORBIDDEN.getMessage());
            });
    }

    @Test
    @WithMockUser(username = TestUserConsts.id, roles = "ADMIN")
    @DisplayName("관리자 권한이 있는 유저 조회 시 200 OK 와 함께 유저 정보 목록을 반환한다")
    void getUsers_ReturnsListOfUserInfo() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
            .andDo(result -> {
                assertThat(result.getResponse().getStatus()).isEqualTo(200);
            });
    }
}