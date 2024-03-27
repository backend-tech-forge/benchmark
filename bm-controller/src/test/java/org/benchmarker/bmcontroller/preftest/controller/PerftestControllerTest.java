package org.benchmarker.bmcontroller.preftest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.bmcontroller.preftest.service.PerftestService;
import org.benchmarker.bmcontroller.template.service.ITestTemplateService;
import org.benchmarker.bmcontroller.user.helper.UserHelper;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(perftestController).build();
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

//    @Test
//    void test21() {
//        WebClient webClient = WebClient.create();
//
//        Flux<ServerSentEvent<CommonTestResult>> eventStream = perftestService.executePerformanceTest(2,
//            "start", webClient,
//            new TemplateInfo(/* mock template info */));
//        StepVerifier.create(eventStream)
//            .expectNextMatches(event -> {
//                // Add your verification logic here
//                return event.data() != null; // For example, check if data is not null
//            })
//            .thenCancel() // Cancel the subscription after receiving one event
//            .verify(); // Verify that the expected events are received
//    }

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

        when(perftestService.executePerformanceTest(Mockito.anyInt(), Mockito.anyString(),
            Mockito.any(), Mockito.any())).thenReturn(mockFlux);

        // Execute the performance test
        Flux<ServerSentEvent<CommonTestResult>> eventStream = perftestService.executePerformanceTest(
            2, "start", WebClient.create(), templateInfo);

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