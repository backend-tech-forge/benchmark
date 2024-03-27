package org.benchmarker.bmagent.pref;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Map;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.junit.jupiter.api.Test;

/**
 * Schel
 */
class HttpSenderTest {
    @Test
    void test() throws MalformedURLException {

        ScheduledTaskService scheduledTaskService = new ScheduledTaskService();
        ResultManagerService resultManagerService = new ResultManagerService();

        HttpSender httpSender = new HttpSender(resultManagerService, scheduledTaskService);

        TemplateInfo get = TemplateInfo.builder()
            .url("http://localhost:8080")
            .method("GET")
            .vuser(5)
            .body(Map.of("1", "2"))
            .headers(Map.of("3", "4"))
            .maxRequest(10)
            .maxDuration(Duration.ofSeconds(10))
            .prepareScript("")
            .build();

        httpSender.sendRequests(get);
        scheduledTaskService.shutdown(0L);

        assertThat(httpSender.getTpsMap()).isNotNull();
        assertThat(httpSender.getMttfbMap()).isNotNull();
        // 5 users * 10 requests = 50
        assertThat(httpSender.getTotalRequests().get()).isEqualTo(50);
    }


}