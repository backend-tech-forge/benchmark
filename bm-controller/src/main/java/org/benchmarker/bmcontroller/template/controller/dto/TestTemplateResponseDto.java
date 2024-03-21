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

    @NotNull
    @JsonProperty("id")
    private Integer id;

    @NotBlank
    @JsonProperty("user_group_id")
    private String userGroupId;

    @NotBlank
    @JsonProperty("url")
    private String url;

    @NotBlank
    @JsonProperty("method")
    private String method;

    @JsonProperty("body")
    private String body;

    @NotNull
    @JsonProperty("vuser")
    private Integer vuser;

    @NotNull
    @JsonProperty("maxRequest")
    private Integer maxRequest;

    @NotNull
    @JsonProperty("maxDuration")
    private Integer maxDuration;

    @NotNull
    @JsonProperty("cpuLimit")
    private Integer cpuLimit;
}
