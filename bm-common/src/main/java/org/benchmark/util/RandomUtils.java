package org.benchmark.util;

import java.util.HashMap;
import org.benchmark.dto.TestResult;

public class RandomUtils {

    // generate random TestResults
    public static TestResult generateRandomTestResult() {
        return TestResult.builder()
            .testId(1)
            .startedAt("2021-01-01 00:00:00")
            .finishedAt("2021-01-01 00:00:00")
            .url("http://localhost:8080")
            .method("GET")
            .totalRequests(100)
            .totalErrors(0)
            .totalSuccess(100)
            .statusCodeCount(new HashMap<String, Integer>() {{
                put("200", 100);
            }})
            .totalUsers(1)
            .totalDuration("1s")
            .mttfbAverage("1ms")
            .MTTFBPercentiles(new HashMap<String, String>() {{
                put("50", "1ms");
                put("95", "1ms");
                put("99", "1ms");
            }})
            .tpsAverage(100.0)
            .TPSPercentiles(new HashMap<String, Double>() {{
                put("50", 100.0);
                put("95", 100.0);
                put("99", 100.0);
            }})
            .build();
    }

}
