package org.benchmarker.template.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempSaveTestResultDto {

    @NotBlank
    @JsonProperty("start_at")
    private long startAt;

    @NotBlank
    @JsonProperty("finish_at")
    private long finishAt;

    @NotNull
    @JsonProperty("error")
    private Integer error;

    @NotNull
    @JsonProperty("success")
    private Integer success;

    @JsonProperty("status_code")
    private Integer statusCode;

    @NotBlank
    @JsonProperty("mttfb_avg")
    private Double mttbfbAvg;

    @NotNull
    @JsonProperty("tps_avg")
    private Double tpsAvg;

}
