package org.benchmarker.bmcontroller.preftest.common;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.util.Randomized;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestInfo implements Randomized<TestInfo> {

    private String groupId;
    private Integer templateId;
    private String testId;
    private AgentStatus testStatus;
    private LocalDateTime startedAt;

    @Override
    public TestInfo random() {
        return TestInfo.builder()
            .testId(UUID.randomUUID().toString())
            .startedAt(LocalDateTime.now())
            .testStatus(AgentStatus.TESTING)
            .templateId(RandomUtils.randInt(0, 100))
            .groupId("defaultGroupId")
            .build();
    }
}
