package org.benchmarker.bmcontroller.preftest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "a", timeToLive = 6000)
public class RunningTest {

    @Id
    private Integer templateId;

    private String groupId;
    private String testId;

}
