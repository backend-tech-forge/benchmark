package org.benchmarker.bmcontroller.preftest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.bmcontroller.agent.AgentServerManager;
import org.benchmarker.bmcontroller.preftest.service.PerftestService;
import org.benchmarker.bmcontroller.template.service.ITestTemplateService;
import org.benchmarker.bmcontroller.user.controller.constant.TestUserConsts;
import org.benchmarker.bmcontroller.user.helper.UserHelper;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class PerftestControllerTest {

    @InjectMocks
    private PerftestController perftestController;
    @Mock
    private PerftestService perftestService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ITestTemplateService testTemplateService;

    @Mock
    private UserContext userContext;

    @Mock
    private AgentServerManager agentServerManager;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(perftestController).build();
    }

    @Test
    @WithMockUser(username = TestUserConsts.id, roles = {"USER"})
    @DisplayName("CommonTestResult SSE 결과반환 테스트")
    public void testSend_Success() throws Exception {
        // given
        User defaultUser = UserHelper.createDefaultUser();
        UserGroup defaultUserGroup = UserHelper.createDefaultUserGroup();

        String userId = defaultUser.getId();
        String groupId = defaultUserGroup.getId();
        Integer templateId = 1;
        String action = "start";
        TemplateInfo templateInfo = new TemplateInfo();
        when(userContext.getCurrentUser()).thenReturn(defaultUser);
        when(testTemplateService.getTemplateInfo(eq(userId), eq(templateId))).thenReturn(
            templateInfo);

        // sse event stubbing
        CommonTestResult randomResult = RandomUtils.generateRandomTestResult();
        ServerSentEvent<CommonTestResult> resultStub = ServerSentEvent.builder(
            randomResult).build();
        Flux<ServerSentEvent<CommonTestResult>> eventStream = Flux.just(resultStub);

        when(perftestService.executePerformanceTest(eq(templateId), eq(groupId), eq(action), any(),
            eq(templateInfo))).thenReturn(eventStream);
        when(agentServerManager.getReadyAgent()).thenReturn(Optional.of(new AgentInfo()));

        // when
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/groups/" + groupId + "/templates/" + templateId)
                    .param("action", action))
            .andExpect(status().isOk());

        // then
        // eventStream 의 subscribe 에서 메세지 send 확인
        verify(messagingTemplate).convertAndSend(eq("/topic/" + groupId + "/" + templateId),
            eq(randomResult));
    }

    @Test
    public void testGetTest() throws Exception {
        // when
        User defaultUser = UserHelper.createDefaultUser();
        when(userContext.getCurrentUser()).thenReturn(defaultUser);
        when(testTemplateService.getTemplateInfo(defaultUser.getId(), 1)).thenReturn(
            new TemplateInfo(/* mock template info */)
        );

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/groups/1/templates/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("template/info"))
            .andExpect(model().attributeExists("groupId", "templateId", "template"));
    }


    @Test
    void testExecutePerformanceTest() {
        // given
        TemplateInfo templateInfo = new TemplateInfo(/* mock template info */);
        List<CommonTestResult> testDatas = new ArrayList<>();

        List<ServerSentEvent<CommonTestResult>> mockEvents = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CommonTestResult testData = RandomUtils.generateRandomTestResult();
            testDatas.add(testData);
            ServerSentEvent<CommonTestResult> mockEvent = ServerSentEvent.builder(testData).build();
            mockEvents.add(mockEvent);
        }

        Flux<ServerSentEvent<CommonTestResult>> mockFlux = Flux.fromIterable(mockEvents);

        when(perftestService.executePerformanceTest(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
            any(), any())).thenReturn(mockFlux);

        // when
        Flux<ServerSentEvent<CommonTestResult>> eventStream = perftestService.executePerformanceTest(1,
            "2", "start", WebClient.create(), templateInfo);

        // then
        // Verify that the mock event is received
        StepVerifier.create(eventStream)
            .expectNextMatches(event -> {
                assertThat(testDatas.contains(event.data())).isTrue();
                return event.data() != null;
            })
            .thenCancel()
            .verify();
    }

}