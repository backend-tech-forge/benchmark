package org.benchmarker.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.user.model.enums.GroupRole;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserGroupRoleInfo {
    @JsonProperty("id")
    private String id;
    @JsonProperty("role")
    private GroupRole role;
}
