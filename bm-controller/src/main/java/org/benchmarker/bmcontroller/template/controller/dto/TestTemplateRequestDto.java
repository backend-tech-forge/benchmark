package org.benchmarker.bmcontroller.template.controller.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^(?i)(GET|PUT|DELETE|POST|PATCH)$", message = "HTTP method must be GET, PUT, DELETE, POST, or PATCH")
    private String method;
    private String body;
    @Min(value = 1, message = "Virtual users must be at least 1")
    @Max(value = 500, message = "Virtual users must be at most 500")
    private Integer vuser;
    @Min(value = 1)
    @Max(value = 1000000)
    private Integer maxRequest;
    // maxDuration is in seconds
    @Max(600)
    private Integer maxDuration;
    private Integer cpuLimit;

    private Map<String, Object> headers;
    private String prepareScript;

    private String name;

    private String description;
  
    public TestTemplate toEntity() throws JsonProcessingException {
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
