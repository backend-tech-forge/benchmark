package org.benchmarker.bmcommon.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempSaveTestResultDto {


    private long startAt;

    private long finishAt;

    private Integer error;

    private Integer success;

    private Integer statusCode;

    private Double mttbfbAvg;

    private Double tpsAvg;

}
