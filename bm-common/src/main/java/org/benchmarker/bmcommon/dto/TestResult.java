package org.benchmarker.bmcommon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TestResult {

    @JsonProperty("test_id")
    private int testId;

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

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("status_code_count")
    private Map<String, Integer> statusCodeCount;

    @JsonProperty("total_users")
    private int totalUsers;

    @JsonProperty("total_duration")
    private String totalDuration;

    @JsonProperty("mttfb_average")
    private double mttfbAverage;

    @JsonProperty("mttfb_percentiles")
    private Map<String, String> MTTFBPercentiles;

    @JsonProperty("tps_average")
    private double tpsAverage;

    @JsonProperty("tps_percentiles")
    private Map<String, Double> TPSPercentiles;

}
