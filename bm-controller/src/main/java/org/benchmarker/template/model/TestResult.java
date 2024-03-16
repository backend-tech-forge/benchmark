package org.benchmarker.template.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.model.BaseTime;
import org.benchmarker.template.controller.dto.TestResultResponseDto;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResult extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_template_id", referencedColumnName = "id", nullable = false)
    private TestTemplate testTemplate;

    private Integer totalRequest;

    private Integer totalError;

    private Integer totalSuccess;

    private Double tps_avg;

    private Double mttbfb_avg;

    public TestResultResponseDto convertToResponseDto() {
        return TestResultResponseDto.builder()
                .id(this.id)
                .userGroupId(this.testTemplate.getUserGroup().getId())
                .totalRequest(this.totalRequest)
                .totalSuccess(this.totalSuccess)
                .totalError(this.totalError)
                .tps_avg(this.tps_avg)
                .mttbfb_avg(this.mttbfb_avg)
                .build();
    }
}
