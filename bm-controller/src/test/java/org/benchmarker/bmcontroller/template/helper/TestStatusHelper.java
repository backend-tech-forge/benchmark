package org.benchmarker.bmcontroller.template.helper;

import org.benchmarker.bmcontroller.template.model.HttpStatusCode;
import org.benchmarker.bmcontroller.template.model.TestStatus;

public class TestStatusHelper {
    public static int defaultCount = 1;
    public static String defaultMessage = "test";
    public static HttpStatusCode defaultCode = HttpStatusCode.GET; // TODO: need to be changed with spring.http.code


    public static TestStatus createDefaultTestStatus() {
        return TestStatus.builder()
            .code(defaultCode)
            .count(defaultCount)
            .message(defaultMessage)
            .build();

    }

}
