package org.benchmarker.template.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.model.BaseTime;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.user.model.UserGroup;

@Slf4j
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTemplate extends BaseTime {

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


    public void update(TestTemplateUpdateDto testTemplate) {

        this.userGroup.update(testTemplate.getUserGroupId());
        this.url = testTemplate.getUrl();
        this.method = testTemplate.getMethod();
        this.body = testTemplate.getBody();
        this.vuser = testTemplate.getVuser();
        this.maxRequest = testTemplate.getMaxRequest();
        this.maxDuration = testTemplate.getMaxDuration();
        this.cpuLimit = testTemplate.getCpuLimit();
    }

    public TestTemplateResponseDto convertToResponseDto() {

        return TestTemplateResponseDto.builder()
                .id(this.id)
                .userGroupId(this.userGroup.getId())
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
