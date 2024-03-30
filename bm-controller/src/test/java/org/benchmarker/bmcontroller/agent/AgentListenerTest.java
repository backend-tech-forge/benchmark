package org.benchmarker.bmcontroller.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.ZonedDateTime;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcontroller.security.JwtTokenProvider;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.util.annotations.RestDocsTest;
import org.util.random.RandomUtil;

@SpringBootTest
@RestDocsTest
class AgentListenerTest {

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.setSecret(RandomUtil.generateRandomString(128));
        jwtTokenProvider.setExpirationTime("1000000");
        jwtTokenProvider.setRefreshExpirationTime("1000000");

    }

    @Autowired
    private AgentListener agentListener;
    @Autowired
    private AgentServerManager agentServerManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Agent listener 로컬 테스트")
    void test() throws Exception {
        String test = jwtTokenProvider.createAccessToken("test", Role.ROLE_USER);
        AgentInfo agentInfo = new AgentInfo();
        mockMvc.perform(post("/api/agent/register")
                .header("Authorization", "Bearer " + test)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(agentInfo)))
            .andDo(response -> {
                System.out.println(response.getResponse().getContentAsString());
            });
    }

    @Test
    @DisplayName("Agent listener 잘못된 토큰 401 반환 테스트")
    void test2() throws Exception {
        AgentInfo agentInfo = AgentInfo.builder().status(AgentStatus.READY).serverUrl("")
            .cpuUsage(1D).memoryUsage(1D).startedAt(
                ZonedDateTime.now()).build();
        mockMvc.perform(post("/api/agent/register")
                .header("Authorization", "Bearer " + "wrong token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(agentInfo)))
            .andDo(response -> {
                MockHttpServletResponse resp = response.getResponse();
                assertThat(resp.getStatus()).isEqualTo(401);
            });
    }

    @Test
    @DisplayName("Agent listener header 없을 때 401 반환 테스트")
    void test3() throws Exception {
        AgentInfo agentInfo = AgentInfo.builder().status(AgentStatus.READY).serverUrl("")
            .cpuUsage(1D).memoryUsage(1D).startedAt(
                ZonedDateTime.now()).build();
        mockMvc.perform(post("/api/agent/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(agentInfo)))
            .andDo(response -> {
                MockHttpServletResponse resp = response.getResponse();
                assertThat(resp.getStatus()).isEqualTo(401);
            });
    }

}