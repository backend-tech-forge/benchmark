package org.benchmarker.bmagent;

import java.time.ZonedDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.util.Randomized;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentInfo implements Randomized<AgentInfo> {
    private AgentStatus status;
    private Set<Long> templateId;
    private double cpuUsage;
    private double memoryUsage;
    private String serverUrl;
    private ZonedDateTime startedAt;

    @Override
    public AgentInfo random() {
        return AgentInfo.builder()
            .status(AgentStatus.READY)
            .startedAt(ZonedDateTime.now())
            .memoryUsage(RandomUtils.randDouble(1,10))
            .cpuUsage(RandomUtils.randDouble(1,10))
            .serverUrl(RandomUtils.randString(10))
            .templateId(Set.of(
                RandomUtils.randLong(1,5),
                RandomUtils.randLong(6,10),
                RandomUtils.randLong(11,15)
            ))
            .build();
    }
}
