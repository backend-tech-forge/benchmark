package org.benchmarker.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.login.controller.dto.LoginRequestInfo;
import org.benchmarker.login.service.LoginService;
import org.benchmarker.user.controller.constant.TestUserConsts;
import org.benchmarker.user.model.User;
import org.benchmarker.user.service.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.util.annotations.RestDocsTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RestDocsTest
public class LoginApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserContext userContext;
    @MockBean
    private LoginService loginService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("로그인 성공")
    public void test1() throws Exception {
        // given
        LoginRequestInfo req = new LoginRequestInfo("username", "password");
        String accessToken = "fakeAccessToken";
        HttpServletResponse resp = new MockHttpServletResponse();

        // when
        when(loginService.login(any())).thenReturn(accessToken);

        // then
        mockMvc.perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(content().string(accessToken));
    }

    @Test
    @DisplayName("로그인 실패 시 GlobalException 발생")
    public void test2() throws Exception {
        // given
        LoginRequestInfo req = new LoginRequestInfo("username", "password");
        String accessToken = "fakeAccessToken";
        HttpServletResponse resp = new MockHttpServletResponse();

        // when
        when(loginService.login(any())).thenThrow(new GlobalException(ErrorCode.USER_NOT_FOUND));

        // then
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
            .andExpect(status().is(ErrorCode.USER_NOT_FOUND.getHttpStatus()))
            .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    @Test
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    @DisplayName("로그아웃 성공")
    public void test3() throws Exception {
        // given
        User user = User.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .build();

        // when
        when(userContext.getCurrentUser()).thenReturn(user);

        // then
        mockMvc.perform(post("/api/logout"))
            .andExpect(status().isOk())
            .andDo(result -> {
                Cookie[] cookies = result.getResponse().getCookies();
                assertThat(cookies).isNotNull();
                assertThat(cookies.length).isEqualTo(1);
                assertThat(cookies[0].getName()).isEqualTo("accessToken");
                assertThat(cookies[0].getValue()).isEqualTo("");
                assertThat(cookies[0].getMaxAge()).isEqualTo(0);
            });
    }

    @Test
    @WithMockUser(username = TestUserConsts.id, roles = "ANONYMOUS")
    @DisplayName("로그아웃 실패 시 403 FORBIDDEN 발생")
    public void test4() throws Exception {
        // given
        User user = User.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .build();

        // when & then
        mockMvc.perform(post("/api/logout"))
            .andExpect(status().is(ErrorCode.FORBIDDEN.getHttpStatus()))
            .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN.getMessage()))
            .andExpect(jsonPath("$.status").value(ErrorCode.FORBIDDEN.getHttpStatus()))
            .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.name()));
    }
}
