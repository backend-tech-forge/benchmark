package org.benchmarker.bmcontroller.template.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveResultReqDto {

    @NotNull
    @JsonProperty("test_id")
    private Integer testId;

    @NotBlank
    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @NotBlank
    @JsonProperty("finished_at")
    private LocalDateTime finishedAt;

    @NotBlank
    @JsonProperty("url")
    private String url;

    @NotBlank
    @JsonProperty("method")
    private String method;

    @NotBlank
    @JsonProperty("status_code")
    private int statusCode;

    @NotNull
    @JsonProperty("total_request")
    private Integer totalRequest;

    @NotNull
    @JsonProperty("total_error")
    private Integer totalError;

    @NotNull
    @JsonProperty("total_success")
    private Integer totalSuccess;

    @NotNull
    @JsonProperty("total_users")
    private Integer totalUsers;

    @NotBlank
    @JsonProperty("mttfb_avg")
    private Double mttbfbAvg;

    @NotNull
    @JsonProperty("tps_avg")
    private Double tpsAvg;

}
