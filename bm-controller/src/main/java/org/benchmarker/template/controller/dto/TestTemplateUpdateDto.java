package org.benchmarker.template.controller.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTemplateUpdateDto {

    private Integer id;

    private String userGroupName;

    private String url;

    private String method;

    private String body;

    private Integer vuser;

    private Integer maxRequest;

    private Integer maxDuration;

    private Integer cpuLimit;

}
