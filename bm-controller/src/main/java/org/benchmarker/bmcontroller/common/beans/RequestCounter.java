package org.benchmarker.bmcontroller.common.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RequestCounter {

    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger totalSuccesses = new AtomicInteger(0);
    private final AtomicInteger totalErrors = new AtomicInteger(0);

    // 요청이 발생할 때마다 호출하여 요청 카운터를 증가시키는 메서드
    public void incrementTotalRequests() {
        totalRequests.incrementAndGet();
    }

    public void incrementTotalSuccesses() {
        totalSuccesses.incrementAndGet();
    }

    public void incrementTotalErrors() {
        totalErrors.incrementAndGet();
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public int getTotalSuccesses() {
        return totalSuccesses.get();
    }

    public int getTotalErrors() {
        return totalErrors.get();
    }

    public void reset() {
        this.totalRequests.set(0);
        this.totalSuccesses.set(0);
        this.totalErrors.set(0);
    }

    public void updateTotalResult(HttpStatusCode statusCode) {

        this.incrementTotalRequests();

        if (statusCode.is2xxSuccessful()) {
            this.incrementTotalSuccesses();
        } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
            this.incrementTotalErrors();
        }
    }
}
