package org.benchmarker.bmagent.pref;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Map;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.status.AgentStatusManager;

import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.util.MockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Schel
 */
class HttpSenderTest extends MockServer {

    private ScheduledTaskService scheduledTaskService = new ScheduledTaskService();
    private ResultManagerService resultManagerService = new ResultManagerService();
    private AgentStatusManager agentStatusManager = new AgentStatusManager();

    @BeforeEach
    void setup() {
        ScheduledTaskService scheduledTaskService = new ScheduledTaskService();
        ResultManagerService resultManagerService = new ResultManagerService();
    }

    @Test
    @DisplayName("url 포멧 에러 시 에러 반환")
    void test2() throws MalformedURLException {
        // given
        HttpSender httpSender = new HttpSender(resultManagerService, scheduledTaskService,
            agentStatusManager);

        TemplateInfo get = TemplateInfo.builder()
            .id("1")
            .url("wrong url")
            .method("GET")
            .vuser(5)
            .body(Map.of("1", "2"))
            .headers(Map.of("3", "4"))
            .maxRequest(10)
            .maxDuration(Duration.ofSeconds(10))
            .prepareScript("a")
            .build();

        // then

        assertThrows((MalformedURLException.class), () -> {
            // when
            httpSender.sendRequests(new SseEmitter(),get);
            httpSender.cancelRequests();
        });


    }

    @Test
    @DisplayName("performance testing")
    void test() throws MalformedURLException {
        // given

        HttpSender httpSender = new HttpSender(resultManagerService, scheduledTaskService,
            agentStatusManager);
        addMockResponse("ok", 50); // mock 50 response

        TemplateInfo get = TemplateInfo.builder()
            .id("1")
            .url(mockServer.url("/").toString())
            .method("GET")
            .vuser(5)
            .body(Map.of("1", "2"))
            .headers(Map.of("3", "4"))
            .maxRequest(10)
            .maxDuration(Duration.ofHours(1))
            .prepareScript("a")
            .build();

        // when
        httpSender.sendRequests(new SseEmitter(),get);
        scheduledTaskService.shutdown(1L);

        // then
        assertThat(httpSender.getTpsMap()).isNotNull();
        assertThat(httpSender.getMttfbMap()).isNotNull();
        // 5 users * 10 requests = 50
        assertThat(httpSender.getTotalRequests().get()).isEqualTo(50);
    }


}