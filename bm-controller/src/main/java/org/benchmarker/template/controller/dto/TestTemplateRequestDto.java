package org.benchmarker.template.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.benchmarker.template.model.TestTemplate;
import org.benchmarker.user.model.UserGroup;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTemplateRequestDto {

    @NotBlank
    private String userGroupName;

    @NotBlank
    private String url;

    @NotBlank
    private String method;

    private String body;

    @NotNull
    private Integer vuser;

    @NotNull
    private Integer maxRequest;

    @NotNull
    private Integer maxDuration;

    @NotNull
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
