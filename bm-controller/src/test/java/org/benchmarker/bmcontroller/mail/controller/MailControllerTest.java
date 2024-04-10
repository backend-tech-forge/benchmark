package org.benchmarker.bmcontroller.mail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailCertificationDto;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailResDto;
import org.benchmarker.bmcontroller.mail.service.impl.MailSenderImpl;
import org.benchmarker.bmcontroller.user.controller.constant.TestUserConsts;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.util.annotations.RestDocsTest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

    @Test
    @DisplayName("메일 Send 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void mailSender() throws Exception {

        //given
        EmailCertificationDto request = EmailCertificationDto.builder()
                .email("test.com")
                .build();

        EmailResDto res = EmailResDto.builder()
                .mail(request.getEmail())
                .certificationCode("123456")
                .build();


        // when
        when(mailSender.sendMail(any())).thenReturn(res);

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

}