package org.benchmarker.bmcontroller.template.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcontroller.common.model.BaseTime;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;

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

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "test_template_id", referencedColumnName = "id", nullable = false)
//    private TestTemplate testTemplate;

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
//                .mttbfbAvg(this.mttbfbAvg)
                .build();
    }

}
