package org.benchmarker.template.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestTemplateResponseDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("user_group_name")
    private String userGroupName;

    @JsonProperty("url")
    private String url;

    @JsonProperty("method")
    private String method;

    @JsonProperty("body")
    private String body;

    @JsonProperty("vuser")
    private Integer vuser;

    @JsonProperty("maxRequest")
    private Integer maxRequest;

    @JsonProperty("maxDuration")
    private Integer maxDuration;

    @JsonProperty("cpuLimit")
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
