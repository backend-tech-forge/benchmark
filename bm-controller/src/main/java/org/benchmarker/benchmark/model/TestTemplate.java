package org.benchmarker.benchmark.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import org.benchmarker.common.model.BaseTime;
import org.benchmarker.user.model.UserGroup;

@Entity
@Table(name = "test_template")
public class TestTemplate extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_group_id", nullable = false)
    private UserGroup userGroup;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String body;

    @Column(nullable = false)
    private Integer maxRequest;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Integer cpuLimit;

    @OneToMany(mappedBy = "testTemplate")
    private List<Result> results;
}
