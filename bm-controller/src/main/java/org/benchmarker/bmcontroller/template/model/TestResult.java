package org.benchmarker.bmcontroller.template.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.model.BaseTime;
import org.benchmarker.bmcontroller.template.controller.dto.ResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;

import java.time.LocalDateTime;
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

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime startedAt;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime finishedAt;

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

    public ResultResDto convertToResponseDto() {
        return ResultResDto.builder()
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

    public SaveResultResDto convertToSaveResDto() {

        return SaveResultResDto.builder()
                .id(this.id)
                .testId(this.getTestTemplate().getId())
                .startedAt(this.startedAt)
                .finishedAt(this.finishedAt)
                .url(this.getTestTemplate().getUrl())
                .method(this.getTestTemplate().getMethod())
                .totalRequest(this.getTotalRequest())
                .totalSuccess(this.getTotalSuccess())
                .totalError(this.getTotalError())
                .mttbfbAvg(this.getMttbfbAvg())
                .tpsAvg(this.getTpsAvg())
                .build();
    }
}
