package org.benchmarker.template.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.model.BaseTime;
import org.benchmarker.template.controller.dto.TestResultResponseDto;

import java.util.List;

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

    private Double tpsAvg;

    private Double mttbfbAvg;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<TestMttfb> testMttfbs;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<TestTps> testTps;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<TestStatus> testStatuses;

    public TestResultResponseDto convertToResponseDto() {
        return TestResultResponseDto.builder()
                .testId(this.id)
                .totalRequest(this.totalRequest)
                .totalSuccess(this.totalSuccess)
                .totalError(this.totalError)
                .tpsAvg(this.tpsAvg)
                .mttbfbAvg(this.mttbfbAvg)
                .build();
    }

    public void resultUpdate(int totalRequest, int totalSuccess, int totalError, Double tpsAvg, Double mttbfbAvg) {
        this.totalRequest = totalRequest;
        this.totalSuccess = totalSuccess;
        this.totalError = totalError;
        this.tpsAvg = tpsAvg;
        this.mttbfbAvg = mttbfbAvg;
    }
}
