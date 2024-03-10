package org.benchmarker.template.controller.dto;

import lombok.*;
import org.benchmarker.template.model.TestTemplate;
import org.benchmarker.user.model.UserGroup;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTemplateRequestDto {

    private String userGroupName;

    private String url;

    private String method;

    private String body;

    private Integer vuser;

    private Integer maxRequest;

    private Integer maxDuration;

    private Integer cpuLimit;

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
