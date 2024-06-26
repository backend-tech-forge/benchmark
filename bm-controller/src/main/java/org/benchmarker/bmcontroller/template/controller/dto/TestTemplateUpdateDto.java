package org.benchmarker.bmcontroller.template.controller.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTemplateUpdateDto {

    private Integer id;

    private String userGroupId;

    private String url;

    private String method;

    private String body;

    private Integer vuser;

    private Integer maxRequest;

    private Integer maxDuration;

    private Integer cpuLimit;

    private String name;

    private String description;

}
