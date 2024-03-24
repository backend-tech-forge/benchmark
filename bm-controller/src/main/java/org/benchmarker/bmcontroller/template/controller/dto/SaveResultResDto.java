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
public class SaveResultResDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("test_id")
    private Integer testId;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("finished_at")
    private LocalDateTime finishedAt;

    @JsonProperty("url")
    private String url;

    @JsonProperty("method")
    private String method;

    @JsonProperty("total_request")
    private Integer totalRequest;

    @JsonProperty("total_error")
    private Integer totalError;

    @JsonProperty("total_success")
    private Integer totalSuccess;

    @JsonProperty("mttfb_avg")
    private Double mttbfbAvg;

    @JsonProperty("tps_avg")
    private Double tpsAvg;

}
