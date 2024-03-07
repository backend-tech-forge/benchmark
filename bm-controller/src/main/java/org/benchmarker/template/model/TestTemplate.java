package org.benchmarker.template.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.model.UserGroup;

import java.time.LocalDateTime;


@Slf4j
@Setter
@Getter
@Entity
public class TestTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private UserGroup userGroup;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String method;

    private String body;

    @Column(nullable = false)
    private Integer vuser;

    private Integer maxRequest;

    private Integer maxDuration;

    @Column(nullable = false)
    private Integer cpuLimit;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime createdAt;

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime updatedAt;

    public TestTemplate() {

    }
}
