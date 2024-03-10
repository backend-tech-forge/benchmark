package org.benchmarker.template.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestTemplateResponseDto {

    private Integer id;

    private String userGroupName;

    private String url;

    private String method;

    private String body;

    private Integer vuser;

    private Integer maxRequest;

    private Integer maxDuration;

    private Integer cpuLimit;

    @Builder
    public TestTemplateResponseDto(Integer id, String userGroupName, String url, String method, String body, Integer vuser, Integer maxRequest, Integer maxDuration, Integer cpuLimit) {
        this.id = id;
        this.userGroupName = userGroupName;
        this.url = url;
        this.method = method;
        this.body = body;
        this.vuser = vuser;
        this.maxRequest = maxRequest;
        this.maxDuration = maxDuration;
        this.cpuLimit = cpuLimit;
    }
}
