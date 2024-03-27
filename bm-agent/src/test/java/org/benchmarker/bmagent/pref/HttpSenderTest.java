package org.benchmarker.bmagent.pref;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Map;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.util.MockServer;
import org.junit.jupiter.api.Test;

/**
 * Schel
 */
class HttpSenderTest extends MockServer {
    @Test
    void test() throws MalformedURLException {

        ScheduledTaskService scheduledTaskService = new ScheduledTaskService();
        ResultManagerService resultManagerService = new ResultManagerService();

        HttpSender httpSender = new HttpSender(resultManagerService, scheduledTaskService);
        addMockResponse("ok",50);


        TemplateInfo get = TemplateInfo.builder()
            .id("1")
            .url(mockServer.url("/").toString())
            .method("GET")
            .vuser(5)
            .body(Map.of("1", "2"))
            .headers(Map.of("3", "4"))
            .maxRequest(10)
            .maxDuration(Duration.ofSeconds(10))
            .prepareScript("a")
            .build();

        httpSender.sendRequests(get);
        scheduledTaskService.shutdown(0L);

        assertThat(httpSender.getTpsMap()).isNotNull();
        assertThat(httpSender.getMttfbMap()).isNotNull();
        // 5 users * 10 requests = 50
        assertThat(httpSender.getTotalRequests().get()).isEqualTo(50);
    }


}