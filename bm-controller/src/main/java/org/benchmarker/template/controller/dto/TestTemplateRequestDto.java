package org.benchmarker.template.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.benchmarker.template.model.TestTemplate;
import org.benchmarker.user.model.UserGroup;

@Getter
@Setter
@NoArgsConstructor
public class TestTemplateRequestDto {

    private String userGroupName;

    private String url;

    private String method;

    private String body;

    private Integer vuser;

    private Integer maxRequest;

    private Integer maxDuration;

    private Integer cpuLimit;

    @Builder
    public TestTemplateRequestDto(String userGroupName, String url, String method, String body, Integer vuser, Integer maxRequest, Integer maxDuration, Integer cpuLimit) {
        this.userGroupName = userGroupName;
        this.url = url;
        this.method = method;
        this.body = body;
        this.vuser = vuser;
        this.maxRequest = maxRequest;
        this.maxDuration = maxDuration;
        this.cpuLimit = cpuLimit;
    }

    public TestTemplate toEntity() {
        return TestTemplate.builder()
                .userGroup(UserGroup.builder().name(this.userGroupName).build())
                .url(this.url)
                .method(this.method)
                .body(this.body)
                .vuser(this.vuser)
                .maxRequest(this.maxRequest)
                .maxDuration(this.maxDuration)
                .cpuLimit(this.cpuLimit)
                .build();
    }
}
