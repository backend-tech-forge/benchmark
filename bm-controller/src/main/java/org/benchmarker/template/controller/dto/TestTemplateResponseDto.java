package org.benchmarker.template.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
