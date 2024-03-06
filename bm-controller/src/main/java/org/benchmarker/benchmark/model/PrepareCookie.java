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

@Entity
@Table(name = "prepare_cookie")
public class PrepareCookie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_template_id")
    private TestTemplate testTemplate;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "body", columnDefinition = "jsonb")
    private String body;
}
