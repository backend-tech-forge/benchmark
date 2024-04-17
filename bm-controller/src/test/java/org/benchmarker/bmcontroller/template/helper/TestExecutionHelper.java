package org.benchmarker.bmcontroller.template.helper;

import java.util.ArrayList;
import java.util.UUID;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcontroller.template.model.TestExecution;

public abstract class TestExecutionHelper {
    public static UUID defaultId = UUID.randomUUID();

    /**
     * need to set id
     * @return default testExecution
     */
    public static TestExecution createDefaultTestExecution() {
        return TestExecution.builder()
            .id(defaultId)
            .testTemplate(TemplateHelper.createDefaultTemplate())
            .agentStatus(AgentStatus.TESTING)
            .testResults(new ArrayList<>())
            .build();

    }


}
