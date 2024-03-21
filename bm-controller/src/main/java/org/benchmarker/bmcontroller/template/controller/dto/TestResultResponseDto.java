package org.benchmarker.template.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultResponseDto {

    @NotNull
    @JsonProperty("test_id")
    private Integer testId;

    @NotBlank
    @JsonProperty("started_at")
    private String startedAt;

    @NotBlank
    @JsonProperty("finished_at")
    private String finishedAt;

    @NotBlank
    @JsonProperty("url")
    private String url;

    @NotBlank
    @JsonProperty("method")
    private String method;

    @NotNull
    @JsonProperty("total_request")
    private Integer totalRequest;

    @NotNull
    @JsonProperty("total_error")
    private Integer totalError;

    @NotNull
    @JsonProperty("total_success")
    private Integer totalSuccess;

    @JsonProperty("status_code_count")
    private Map<String, Integer> statusCodeCount;

    @NotNull
    @JsonProperty("total_users")
    private Integer totalUsers;

    @NotBlank
    @JsonProperty("total_duration")
    private String totalDuration;

    @NotBlank
    @JsonProperty("mttfb_avg")
    private Double mttbfbAvg;

    @JsonProperty("MTTFBPercentiles")
    private Map<String, Double> mttfbPercentiles;

    @NotNull
    @JsonProperty("tps_avg")
    private Double tpsAvg;

    @JsonProperty("TPSPercentiles")
    private Map<String, Double> tpsPercentiles;
}
