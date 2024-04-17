package org.benchmarker.bmcontroller.template.helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcontroller.template.model.TestResult;

public abstract class TestResultHelper {

    public static LocalDateTime defaultStartedAt = LocalDateTime.of(2021, 1, 1, 1, 1);
    public static LocalDateTime defaultFinishedAt = LocalDateTime.of(2021, 1, 1, 1, 10);
    public static int defaultTotalRequest = 100;
    public static int defaultTotalSuccess = 90;
    public static int defaultTotalError = 10;
    public static double defaultTpsAvg = 3.0;
    public static String defaultMttbfbAvg = "3";

    public static TestResult createDefaultTestResult() {
        return TestResult.builder()
            .testExecution(TestExecutionHelper.createDefaultTestExecution())
            .testStatuses(new ArrayList<>())
            .testTps(new ArrayList<>())
            .testMttfbs(new ArrayList<>())
            .agentStatus(AgentStatus.TESTING)
            .startedAt(defaultStartedAt)
            .finishedAt(defaultFinishedAt)
            .totalRequest(defaultTotalRequest)
            .totalSuccess(defaultTotalSuccess)
            .totalError(defaultTotalError)
            .tpsAvg(defaultTpsAvg)
            .mttbfbAvg(defaultMttbfbAvg)
            .build();

    }

}
