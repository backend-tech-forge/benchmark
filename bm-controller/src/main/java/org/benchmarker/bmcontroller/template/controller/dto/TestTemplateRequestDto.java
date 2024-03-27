package org.benchmarker.bmcontroller.template.controller.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    // maxDuration is in seconds
    @NotNull
    private Integer maxDuration;
    @NotNull
    private Integer cpuLimit;

    private Map<String, Object> headers;
    private String prepareScript;

    public TestTemplate toEntity() throws JsonProcessingException {

    private String name;

    private String description;
  
    public TestTemplate toEntity() {
        return TestTemplate.builder()
            .userGroup(UserGroup.builder().id(this.userGroupId).build())
            .url(this.url)
            .method(this.method)
            .body(this.body)
            .headers(new ObjectMapper().writeValueAsString(this.headers))
            .prepareScript(this.prepareScript)
            .vuser(this.vuser)
            .maxRequest(this.maxRequest)
            .maxDuration(this.maxDuration)
            .cpuLimit(this.cpuLimit)
            .build();
    }
}
