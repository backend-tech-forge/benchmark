package org.benchmarker.bmcontroller.template.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResDto {

    @JsonProperty("test_id")
    private Integer testId;

    @JsonProperty("started_at")
    private String startedAt;

    @JsonProperty("finished_at")
    private String finishedAt;

    @JsonProperty("url")
    private String url;

    @JsonProperty("body")
    private String body;

    @JsonProperty("method")
    private String method;

    @JsonProperty("total_request")
    private Integer totalRequest;

    @JsonProperty("total_error")
    private Integer totalError;

    @JsonProperty("total_success")
    private Integer totalSuccess;

    @JsonProperty("status_code_count")
    private Map<String, Integer> statusCodeCount;

    @JsonProperty("total_users")
    private Integer totalUsers;

    @JsonProperty("total_duration")
    private String totalDuration;

    @JsonProperty("mttfb_avg")
    private Double mttbfbAvg;

    @JsonProperty("MTTFBPercentiles")
    private Map<String, Double> mttfbPercentiles;

    @JsonProperty("tps_avg")
    private Double tpsAvg;

    @JsonProperty("TPSPercentiles")
    private Map<String, Double> tpsPercentiles;
}
