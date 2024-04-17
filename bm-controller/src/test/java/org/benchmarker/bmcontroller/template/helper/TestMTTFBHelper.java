package org.benchmarker.bmcontroller.template.helper;

import java.time.LocalDateTime;
import org.benchmarker.bmcontroller.template.model.TestMttfb;

public class TestMTTFBHelper {
    public static LocalDateTime defaultStartAt = LocalDateTime.of(2021, 1, 1, 1, 1);
    public static LocalDateTime defaultFinishAt = LocalDateTime.of(2021, 1, 1, 1, 10);
    public static String defaultMttfb = "3";

    public static TestMttfb createDefaultTestMTTFB() {
        return TestMttfb.builder()
            .mttfb(defaultMttfb)
            .startAt(defaultStartAt)
            .finishAt(defaultFinishAt)
            .build();

    }

}
