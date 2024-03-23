package org.benchmarker.bmcontroller.template.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.model.BaseTime;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;


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
    private List<Mttfb> mttfbs;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<Tps> tps;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<TemplateResultStatus> templateResultStatuses;

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
