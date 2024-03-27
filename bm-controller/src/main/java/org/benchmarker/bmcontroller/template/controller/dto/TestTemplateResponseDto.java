package org.benchmarker.bmcontroller.template.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTemplateResponseDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("user_group_id")
    private String userGroupId;

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

    private String name;

    private String description;
}
