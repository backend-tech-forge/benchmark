package org.benchmarker.bmcontroller.template.helper;

import org.benchmarker.bmcontroller.template.model.TestTemplate;

/**
 * Helper class for testing template
 */
public abstract class TemplateHelper {

    public static TestTemplate createDefaultTemplate() {
        return TestTemplate.builder()
                .url("test.com")
                .method("get")
                .body("")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

    }
}
