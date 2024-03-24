package org.benchmarker.bmcontroller.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.benchmarker.bmcontroller.security.JwtTokenProvider;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

        mockMvc.perform(get("/api/endpoint")
                .header("Authorization", "Bearer " + test))
            .andDo(response ->{
                System.out.println(response.getResponse().getContentAsString());
            });
    }

    @Test
    @DisplayName("Agent listener 잘못된 토큰 401 반환 테스트")
    void test2() throws Exception {
        mockMvc.perform(get("/api/endpoint")
                .header("Authorization", "Bearer " + "wrong token"))
            .andDo(response ->{
                MockHttpServletResponse resp = response.getResponse();
                assertThat(resp.getStatus()).isEqualTo(401);
            });
    }

    @Test
    @DisplayName("Agent listener header 없을 때 401 반환 테스트")
    void test3() throws Exception {
        mockMvc.perform(get("/api/endpoint"))
            .andDo(response ->{
                MockHttpServletResponse resp = response.getResponse();
                assertThat(resp.getStatus()).isEqualTo(401);
            });
    }

}