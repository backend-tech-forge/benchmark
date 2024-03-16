package org.benchmarker.common.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestCounterResetScheduler {

    private final RequestCounter requestCounter;

    @Autowired
    public RequestCounterResetScheduler(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void resetRequestCounter() {
        int currentRequestCount = requestCounter.getTotalRequests();
        int currentSuccessCount = requestCounter.getTotalSuccesses();
        int currentErrorCount = requestCounter.getTotalErrors();

        // 요청 카운터를 초기화합니다.
        requestCounter.reset();

        log.info("Reset request counter. Current request count: " + currentRequestCount);
        log.info("Total successes: " + currentSuccessCount);
        log.info("Total errors: " + currentErrorCount);
    }
}
