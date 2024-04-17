package org.benchmarker.bmcontroller.template.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentStatus;
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

    private Integer totalRequest;

    private Integer totalError;

    private Integer totalSuccess;

    private Double tpsAvg;

    private String mttbfbAvg;

    @Column(columnDefinition = "timestamp(6)")
    private LocalDateTime startedAt;

    @Column(columnDefinition = "timestamp(6)")
    private LocalDateTime finishedAt;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id", nullable = false)
    private TestExecution testExecution;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<TestMttfb> testMttfbs;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<TestTps> testTps;

    @OneToMany(mappedBy = "testResult", fetch = FetchType.EAGER)
    private List<TestStatus> testStatuses;

    @Enumerated(EnumType.STRING)
    private AgentStatus agentStatus;

    public TestResultResponseDto convertToResponseDto() {
        return TestResultResponseDto.builder()
                .testId(this.id)
                .totalRequest(this.totalRequest)
                .totalSuccess(this.totalSuccess)
                .totalError(this.totalError)
                .tpsAvg(this.tpsAvg)
                .mttbfbAvg(Double.valueOf(this.mttbfbAvg))
                .build();
    }


}
