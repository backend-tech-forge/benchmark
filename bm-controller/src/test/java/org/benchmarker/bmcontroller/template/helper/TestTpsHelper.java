package org.benchmarker.bmcontroller.template.helper;

import java.time.LocalDateTime;
import org.benchmarker.bmcontroller.template.model.TestTps;

public class TestTpsHelper {
    public static LocalDateTime defaultStartAt = LocalDateTime.of(2021, 1, 1, 1, 1);
    public static LocalDateTime defaultFinishAt = LocalDateTime.of(2021, 1, 1, 1, 10);
    public static double defaultTransaction = 100D;

    public static TestTps createDefaultTestTps() {
        return TestTps.builder()
            .startAt(defaultStartAt)
            .finishAt(defaultFinishAt)
            .transaction(defaultTransaction)
            .build();

    }

}
