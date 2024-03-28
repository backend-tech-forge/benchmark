package org.benchmarker.bmagent.pref;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Map;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.util.MockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Schel
 */
class HttpSenderTest extends MockServer {

    private ScheduledTaskService scheduledTaskService = new ScheduledTaskService();
    private ResultManagerService resultManagerService = new ResultManagerService();

    @BeforeEach
    void setup(){
        ScheduledTaskService scheduledTaskService = new ScheduledTaskService();
        ResultManagerService resultManagerService = new ResultManagerService();
    }

    @Test
    @DisplayName("url 포멧 에러 시 에러 반환")
    void test2() throws MalformedURLException {
        // given
        HttpSender httpSender = new HttpSender(resultManagerService, scheduledTaskService);

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
        assertThrows((MalformedURLException.class),()->{
            // when
            httpSender.sendRequests(get);
        });
    }

    @Test
    @DisplayName("performance testing")
    void test() throws MalformedURLException {
        // given
        HttpSender httpSender = new HttpSender(resultManagerService, scheduledTaskService);
        addMockResponse("ok",50); // mock 50 response

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
        httpSender.sendRequests(get);
        scheduledTaskService.shutdown(0L);

        // then
        assertThat(httpSender.getTpsMap()).isNotNull();
        assertThat(httpSender.getMttfbMap()).isNotNull();
        // 5 users * 10 requests = 50
        assertThat(httpSender.getTotalRequests().get()).isEqualTo(50);
    }


}