package org.benchmarker.bmcontroller.template.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.user.model.UserGroup;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTemplateRequestDto {

    @NotBlank
    private String userGroupId;

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
                .userGroup(UserGroup.builder().id(this.userGroupId).build())
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
