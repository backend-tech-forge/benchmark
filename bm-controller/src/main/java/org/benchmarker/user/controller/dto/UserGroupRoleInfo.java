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

/**
 * {@link UserGroupRoleInfo} is a DTO class has {@link #id}, {@link #role} fields.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserGroupRoleInfo {

    /**
     * user id who is participating in the group
     */
    @JsonProperty("id")
    private String id;
    /**
     * role of the user in the group {@link GroupRole#LEADER} or {@link GroupRole#MEMBER}
     */
    @JsonProperty("role")
    private GroupRole role;
}
