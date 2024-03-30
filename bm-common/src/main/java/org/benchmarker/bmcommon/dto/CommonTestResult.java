package org.benchmarker.bmcommon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.bmagent.AgentStatus;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommonTestResult {

    @JsonProperty("test_id")
    private int testId;
    @JsonProperty("group_id")
    private String groupId;
    @JsonProperty("started_at")
    private String startedAt;
    @JsonProperty("finished_at")
    private String finishedAt;
    private String url;
    private String method;
    @JsonProperty("total_requests")
    private int totalRequests;
    @JsonProperty("total_errors")
    private int totalErrors;
    @JsonProperty("total_success")
    private int totalSuccess;
    @JsonProperty("status_code_count")
    private Map<String, Integer> statusCodeCount;
    @JsonProperty("total_users")
    private int totalUsers;
    @JsonProperty("total_duration")
    private String totalDuration;
    @JsonProperty("mttfb_average")
    private String mttfbAverage;
    @JsonProperty("mttfb_percentiles")
    private Map<Double, Long> MTTFBPercentiles;
    @JsonProperty("tps_average")
    private double tpsAverage;
    @JsonProperty("tps_percentiles")
    private Map<Double, Double> TPSPercentiles;
    @JsonProperty(value = "test_status")
    private AgentStatus testStatus;
}
