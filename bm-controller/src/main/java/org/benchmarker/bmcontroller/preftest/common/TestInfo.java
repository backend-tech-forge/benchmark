package org.benchmarker.bmcontroller.preftest.common;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.bmagent.AgentStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestInfo {
    private String groupId;
    private Integer templateId;
    private String testId;
    private AgentStatus testStatus;
    private LocalDateTime startedAt;
}
