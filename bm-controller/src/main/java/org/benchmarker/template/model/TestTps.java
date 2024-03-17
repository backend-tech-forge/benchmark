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
public class TestTps extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_result_id", referencedColumnName = "id", nullable = false)
    private TestResult testResult;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime startAt;

    private Double transaction;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime finishAt;

}
