package org.benchmarker.template.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.model.UserGroup;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Entity
public class TemplateResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private UserGroup userGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_template_id", referencedColumnName = "id", nullable = false)
    private TestTemplate testTemplate;

    private Integer totalRequest;

    private Integer totalError;

    private Integer totalSuccess;

    private Integer tps_avg;

    private Integer mttbfb_avg;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime createdAt;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime updatedAt;
}
