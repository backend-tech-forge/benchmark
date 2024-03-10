package org.benchmarker.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalErrorResponse;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.controller.constant.TestUserConsts;
import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.model.enums.Role;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.service.UserContext;
import org.benchmarker.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


@SpringBootTest
@RestDocsTest
class UserApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserContext userContext;


    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Nested
    @DisplayName("유저 생성")
    class test3 {

        @Test
        @DisplayName("valid 유저 생성 시 200 UserInfo 응답을 반환한다")
        void test31() throws Exception {
            // Given
            UserRegisterDto userRegisterDto = UserRegisterDto.builder()
                .id(TestUserConsts.id)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .userGroup(List.of())
                .build();
            UserInfo userInfoBuilder = UserInfo.builder()
                .id(TestUserConsts.id)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .userGroup(List.of())
                .build();

            // when
            when(userService.createUser(any())).thenReturn(Optional.of(userInfoBuilder));

            // then
            mockMvc.perform(post("/api/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(userRegisterDto)))
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);
                    UserInfo userInfo = objectMapper.readValue(
                        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        UserInfo.class);
                    assertThat(userInfo.getId()).isEqualTo(TestUserConsts.id);
                    assertThat(userInfo.getEmail()).isEqualTo(TestUserConsts.email);
                    assertThat(userInfo.getSlackWebhookUrl()).isEqualTo(
                        TestUserConsts.slackWebhookUrl);
                });
        }

        @Test
        @DisplayName("valid 유저 생성 시 Group 또한 생성하고 200 UserInfo 응답을 반환한다")
        void test35() throws Exception {
            // Given
            UserGroup userGroup = UserGroup.builder()
                .id("newGroupId")
                .name("group")
                .build();
            UserRegisterDto userRegisterDto = UserRegisterDto.builder()
                .id(TestUserConsts.id)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .userGroup(List.of(userGroup))
                .build();
            UserInfo userInfoBuilder = UserInfo.builder()
                .id(TestUserConsts.id)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .userGroup(List.of(userGroup))
                .build();
            User user = userRegisterDto.toEntity();

            // when
            when(userService.createUser(any())).thenReturn(Optional.of(userInfoBuilder));

            // then
            mockMvc.perform(post("/api/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(userRegisterDto)))
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);
                    UserInfo userInfo = objectMapper.readValue(
                        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        UserInfo.class);
                    assertThat(userInfo.getId()).isEqualTo(TestUserConsts.id);
                    assertThat(userInfo.getEmail()).isEqualTo(TestUserConsts.email);
                    assertThat(userInfo.getSlackWebhookUrl()).isEqualTo(
                        TestUserConsts.slackWebhookUrl);
                });
        }

        @Test
        @DisplayName("중복 id 유저 생성 시 404 USER_ALREADY_EXIST 에러를 반환한다")
        void test32() throws Exception {
            // given
            UserRegisterDto userRegisterDto = UserRegisterDto.builder()
                .id(TestUserConsts.id)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .build();

            // when
            when(userService.createUser(any())).thenThrow(
                new GlobalException(ErrorCode.USER_ALREADY_EXIST));

            // then
            mockMvc.perform(post("/api/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(userRegisterDto)))
                .andExpect(status().is(ErrorCode.USER_ALREADY_EXIST.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_ALREADY_EXIST.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_ALREADY_EXIST.name()));
        }
    }


    @Nested
    @DisplayName("유저 조회")
    class test1 {

        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "USER")
        @DisplayName("내 정보 조회 시 200 UserInfo 를 반환한다")
        void test11() throws Exception {
            // given
            String userId = TestUserConsts.id;
            User userStub = User.builder()
                .id(userId)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .build();

            // when & then
            when(userService.getUser(userId)).thenReturn(Optional.of(UserInfo.builder()
                .id(userId)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .slackNotification(false)
                .emailNotification(false)
                .userGroup(List.of(UserGroup.builder().id("default").name("default").build()))
                .build()));
            when(userContext.getCurrentUser()).thenReturn(userStub);

            mockMvc.perform(get("/api/user"))
                .andDo(result -> {
                }).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUserConsts.id))
                .andExpect(jsonPath("$.email").value(TestUserConsts.email))
                .andExpect(jsonPath("$.slack_webhook_url").value(TestUserConsts.slackWebhookUrl))
                .andExpect(jsonPath("$.slack_notification").value(false))
                .andExpect(jsonPath("$.email_notification").value(false));
        }

        @Test
        @WithMockUser(username = "anonymous_user", roles = "ANONYMOUS")
        @DisplayName("내 정보 조회 시 권한이 없으면 403 FORBIDDEN 반환한다")
        void test12() throws Exception {
            // when & then
            when(userContext.getCurrentUser()).thenReturn(null);

            mockMvc.perform(get("/api/user"))
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(403);
                })
                .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.name()));
        }

        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "USER")
        @DisplayName("유저 정보 조회 시 같은 그룹 내 유저라면 200 UserInfo 를 반환한다")
        void test13() throws Exception {
            // given
            String userId = TestUserConsts.id;
            String otherUserId = "otherUserId";
            User userStub = User.builder()
                .id(userId)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .build();

            // when & then
            when(userContext.getCurrentUser()).thenReturn(userStub);
            when(userService.getUserIfSameGroup(TestUserConsts.id, otherUserId)).thenReturn(
                UserInfo.builder()
                    .id(otherUserId)
                    .email(TestUserConsts.email)
                    .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                    .slackNotification(false)
                    .emailNotification(false)
                    .userGroup(List.of(UserGroup.builder().id("default").name("default").build()))
                    .build());

            mockMvc.perform(get("/api/users/" + otherUserId))
                .andDo(result -> {
                }).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(otherUserId))
                .andExpect(jsonPath("$.email").value(TestUserConsts.email))
                .andExpect(jsonPath("$.slack_webhook_url").value(TestUserConsts.slackWebhookUrl))
                .andExpect(jsonPath("$.slack_notification").value(false))
                .andExpect(jsonPath("$.email_notification").value(false));
        }

        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "USER")
        @DisplayName("유저 정보 조회 시 같은 그룹 내 유저가 아니면 400 USER_NOT_SAME_GROUP 에러를 반환한다")
        void test14() throws Exception {
            // given
            String userId = TestUserConsts.id;
            String otherUserId = "otherUserId";
            User userStub = User.builder()
                .id(userId)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .build();

            // when & then
            when(userContext.getCurrentUser()).thenReturn(userStub);
            when(userService.getUserIfSameGroup(userId, otherUserId)).thenThrow(
                new GlobalException(ErrorCode.USER_NOT_SAME_GROUP));

            mockMvc.perform(get("/api/users/" + otherUserId))
                .andDo(result -> {
                })
                .andExpect(status().is(ErrorCode.USER_NOT_SAME_GROUP.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_SAME_GROUP.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_SAME_GROUP.name()));
        }

        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "ADMIN")
        @DisplayName("ADMIN 권한으로 유저 정보 조회 시 같은 그룹 내 유저가 아니더라도 200 UserInfo 를 반환한다")
        void test15() throws Exception {
            // given
            String userId = TestUserConsts.id;
            String otherUserId = "otherUserId";
            User userStub = User.builder()
                .id(userId)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .role(Role.ROLE_ADMIN)
                .build();
            UserInfo userInfoStub = UserInfo.builder()
                .id(otherUserId)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .slackNotification(false)
                .emailNotification(false)
                .userGroup(List.of(UserGroup.builder().id("default").name("default").build()))
                .build();

            // when & then
            when(userContext.getCurrentUser()).thenReturn(userStub);
            when(userService.getUser(otherUserId)).thenReturn(Optional.of(userInfoStub));

            mockMvc.perform(get("/api/users/" + otherUserId))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(otherUserId))
                .andExpect(jsonPath("$.email").value(TestUserConsts.email))
                .andExpect(jsonPath("$.slack_webhook_url").value(TestUserConsts.slackWebhookUrl))
                .andExpect(jsonPath("$.slack_notification").value(false))
                .andExpect(jsonPath("$.email_notification").value(false));
        }
    }


    @Nested
    @DisplayName("유저리스트 조회")
    class test01 {
        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "USER")
        @DisplayName("USER 권한으로 전체 유저리스트 조회 시 403 FORBIDDEN 에러 반환")
        void getUsersWithUser_ReturnsListOfUserInfo() throws Exception {
            // when & then
            mockMvc.perform(get("/api/users"))
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(403);

                    GlobalErrorResponse ex = objectMapper.readValue(
                        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        GlobalErrorResponse.class);
                    System.out.println("ex = " + ex);

                    assertThat(ex.getCode()).isEqualTo(ErrorCode.FORBIDDEN.name());
                    assertThat(ex.getMessage()).isEqualTo(ErrorCode.FORBIDDEN.getMessage());
                });
        }

        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "ADMIN")
        @DisplayName("ADMIN 권한으로 전체 유저리스트 조회 시 200 OK 을 반환한다")
        void getUsers_ReturnsListOfUserInfo() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/users"))
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);
                });
        }
    }

    @Nested
    @DisplayName("유저 삭제")
    class test04 {
        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "USER")
        @DisplayName("현재 유저 삭제 시 200 OK 를 반환한다")
        void test41() throws Exception {
            // given
            String userId = TestUserConsts.id;
            User userStub = User.builder()
                .id(userId)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .build();

            // when & then
            when(userContext.getCurrentUser()).thenReturn(userStub);
            doNothing().when(userService).deleteUser(any());

            mockMvc.perform(delete("/api/user"))
                .andDo(result -> {
                })
                .andExpect(status().is(200));
        }

        @Test
        @WithMockUser(username = TestUserConsts.id, roles = "ADMIN")
        @DisplayName("ADMIN 이 유저 지정 삭제 시 200 OK 를 반환한다")
        void test42() throws Exception {
            // given
            String userId = TestUserConsts.id;
            String otherUserId = "otherUserId";
            User userStub = User.builder()
                .id(userId)
                .password(TestUserConsts.password)
                .email(TestUserConsts.email)
                .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
                .build();

            // when & then
            when(userContext.getCurrentUser()).thenReturn(userStub);
            doNothing().when(userService).deleteUser(any());

            mockMvc.perform(delete("/api/users/"+otherUserId))
                .andDo(result -> {
                })
                .andExpect(status().is(200));
        }
    }

}