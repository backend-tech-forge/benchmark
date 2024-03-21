package org.benchmarker.bmcontroller.user.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;

/**
 * {@link GroupInfo} is a DTO class for the GroupInfo object.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class GroupInfo {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("users")
    private List<UserGroupRoleInfo> users; // This is a list of user ids
    @JsonProperty("templates")
    private List<TestTemplateResponseDto> templates;
}
