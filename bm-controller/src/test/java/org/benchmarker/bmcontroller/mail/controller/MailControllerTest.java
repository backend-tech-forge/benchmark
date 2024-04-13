package org.benchmarker.bmcontroller.mail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.mail.controller.dto.*;
import org.benchmarker.bmcontroller.mail.service.impl.MailSenderImpl;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.util.annotations.RestDocsTest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@RestDocsTest
class MailControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailSenderImpl mailSender;

    @MockBean
    UserContext userContext;

    @MockBean
    private MockHttpSession httpSession;

    @AfterEach
    public void clean(){
        httpSession.clearAttributes();
    }

    @Test
    @DisplayName("메일 Send 테스트")
    public void mailSender() throws Exception {

        //given
        EmailCertificationDto request = EmailCertificationDto.builder()
                .email("test@Naver.com")
                .build();

        EmailResDto res = EmailResDto.builder()
                .mail(request.getEmail())
                .certificationCode("123456")
                .build();


        // when
        when(mailSender.sendMail(any(), any())).thenReturn(res);

        // then
        mockMvc.perform(post("/api/mail/certification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    EmailResDto resEmailInfo = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            EmailResDto.class);

                    assertThat(resEmailInfo.getMail()).isEqualTo(request.getEmail());
                    assertThat(resEmailInfo.getCertificationCode()).isEqualTo("123456");
                });
    }

    @Test
    @DisplayName("정확하지 않은 이메일 형식일 경우 에러 발생 테스트")
    public void mailFormatErrorTest() throws Exception {

        //given
        EmailCertificationDto request = EmailCertificationDto.builder()
                .email("test.Naver.com")
                .build();

        EmailResDto res = EmailResDto.builder()
                .mail(request.getEmail())
                .certificationCode("123456")
                .build();


        // when
        when(mailSender.sendMail(any(), any())).thenReturn(res);

        // then
        mockMvc.perform(post("/api/mail/certification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.BAD_REQUEST.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BAD_REQUEST.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.BAD_REQUEST.name()));
    }

    @Test
    @DisplayName("메일 인증 코드 비교 테스트")
    public void mailCertification() throws Exception {

        //given
        EmailCodeDto req = EmailCodeDto.builder()
                .code("123456")
                .build();

        UserSessionInfo userSessionInfo = UserSessionInfo.builder()
                .userMail("test@naver.com")
                .certificationCode("123456")
                .isVerification(false)
                .verificationTime(LocalDateTime.now())
                .build();

        httpSession = new MockHttpSession();
        httpSession.setAttribute("userSessionInfo", userSessionInfo);

        // when & then
        mockMvc.perform(post("/api/mail/certification/code")
                        .session(httpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    EmailCodeCertificationResultDto resEmailInfo = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            EmailCodeCertificationResultDto.class);

                    assertThat(resEmailInfo.getStatus()).isEqualTo("success");
                    assertThat(resEmailInfo.getMessage()).isEqualTo("Authentication successful!!");
                });
    }

    @Test
    @DisplayName("session 값이 Null 일 경우 에러 테스트")
    public void sessionInfoNullExceptionTest() throws Exception {

        //given
        EmailCodeDto req = EmailCodeDto.builder()
                .code("123456")
                .build();

        when(httpSession.getAttribute("userSessionInfo")).thenReturn(null);

        // when & then
        mockMvc.perform(post("/api/mail/certification/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    EmailCodeCertificationResultDto resEmailInfo = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            EmailCodeCertificationResultDto.class);

                    assertThat(resEmailInfo.getStatus()).isEqualTo("fail");
                    assertThat(resEmailInfo.getMessage()).isEqualTo("Session expired or invalid. Please try again.");
                });
    }

    @Test
    @DisplayName("인증 번호가 틀렸을 때 에러 테스트")
    public void certificationNumberExceptionTest() throws Exception {

        //given
        EmailCodeDto req = EmailCodeDto.builder()
                .code("123456")
                .build();

        UserSessionInfo userSessionInfo = UserSessionInfo.builder()
                .userMail("test@naver.com")
                .certificationCode("123457")
                .isVerification(false)
                .verificationTime(LocalDateTime.now())
                .build();

        httpSession = new MockHttpSession();
        httpSession.setAttribute("userSessionInfo", userSessionInfo);

        // when & then
        mockMvc.perform(post("/api/mail/certification/code")
                        .session(httpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    EmailCodeCertificationResultDto resEmailInfo = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            EmailCodeCertificationResultDto.class);

                    assertThat(resEmailInfo.getStatus()).isEqualTo("fail");
                    assertThat(resEmailInfo.getMessage()).isEqualTo("Please enter the authentication number correctly");
                });
    }

}