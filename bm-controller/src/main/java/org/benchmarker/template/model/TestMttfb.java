package org.benchmarker.template.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.model.BaseTime;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestMttfb extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_result_id", referencedColumnName = "id", nullable = false)
    private TestResult testResult;

    private Double mttfb;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime startedAt;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime finishedAt;

}
