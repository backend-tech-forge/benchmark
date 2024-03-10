package org.benchmarker.template.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.user.model.UserGroup;

import java.time.LocalDateTime;

@Slf4j
@Setter
@Getter
@Entity
public class TestTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @Builder
    public TestTemplate(UserGroup userGroup, String url, String method, String body, Integer vuser, Integer maxRequest, Integer maxDuration, Integer cpuLimit) {
        this.userGroup = userGroup;
        this.url = url;
        this.method = method;
        this.body = body;
        this.vuser = vuser;
        this.maxRequest = maxRequest;
        this.maxDuration = maxDuration;
        this.cpuLimit = cpuLimit;

        this.createdAt = LocalDateTime.now();
    }

    public void update(TestTemplateUpdateDto testTemplate) {

        this.userGroup.update(testTemplate.getUserGroupName());
        this.url = testTemplate.getUrl();
        this.method = testTemplate.getMethod();
        this.body = testTemplate.getBody();
        this.vuser = testTemplate.getVuser();
        this.maxRequest = testTemplate.getMaxRequest();
        this.maxDuration = testTemplate.getMaxDuration();
        this.cpuLimit = testTemplate.getCpuLimit();

        this.updatedAt = LocalDateTime.now();
    }

    public TestTemplateResponseDto convertToResponseDto() {

        return TestTemplateResponseDto.builder()
                .id(this.id)
                .userGroupName(this.userGroup.getName())
                .method(this.method)
                .url(this.url)
                .body(this.body)
                .vuser(this.vuser)
                .maxRequest(this.maxRequest)
                .maxDuration(this.maxDuration)
                .cpuLimit(this.cpuLimit)
                .build();
    }
}
