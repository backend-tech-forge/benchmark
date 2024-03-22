package org.benchmarker.bmcommon.util;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcommon.dto.TestResult;

public class RandomUtils {

    // generate random TestResults
    public static TestResult generateRandomTestResult() {
        return TestResult.builder()
            .testId(1)
            .testStatus(AgentStatus.TESTING)
            .startedAt("2021-01-01 00:00:00")
            .finishedAt("2021-01-01 00:00:00")
            .url("http://localhost:8080")
            .method("GET")
            .totalRequests(randInt(100,200))
            .totalErrors(randInt(0,100))
            .totalSuccess(randInt(0,100))
            .statusCodeCount(new HashMap<String, Integer>() {{
                put("200", randInt(0,100));
                put("400", randInt(0,100));
                put("500", randInt(0,100));
            }})
            .totalUsers(1)
            .totalDuration("1s")
            .mttfbAverage("1ms")
            .MTTFBPercentiles(new HashMap<String, String>() {{
                put("50", randMS(1,20));
                put("95", randMS(20,40));
                put("99", randMS(40,60));
            }})
            .tpsAverage(randDouble(0, 100))
            .TPSPercentiles(new HashMap<String, Double>() {{
                put("50", randDouble(0, 100));
                put("95", randDouble(0, 100));
                put("99", randDouble(0, 100));
            }})
            .build();
    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String randMS(int min, int max) {
        return randInt(min, max) + "ms";
    }

    public static long randLong(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    public static double randDouble(long min, long max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

}
