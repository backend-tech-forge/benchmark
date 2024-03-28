package org.benchmarker.bmagent;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentInfo {
    private AgentStatus status;
    private double cpuUsage;
    private double memoryUsage;
    private String serverUrl;
    private ZonedDateTime startedAt;
}
