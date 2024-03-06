package org.benchmarker.benchmark.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.benchmarker.user.model.UserGroup;

@Entity
@Table(name = "result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_template_id", nullable = false)
    private TestTemplate testTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_group_id", nullable = false)
    private UserGroup userGroupId;

    @Column(name = "total_request")
    private Integer totalRequest;

    @Column(name = "total_error")
    private Integer totalError;

    @Column(name = "total_success")
    private Integer totalSuccess;

    @Column(name = "tps", columnDefinition = "bigint")
    private Long tps;

    @Column(name = "mttrb_avg", columnDefinition = "bigint")
    private Long mttrbAvg;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime finishedAt;
}
