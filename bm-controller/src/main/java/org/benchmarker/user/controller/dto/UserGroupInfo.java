package org.benchmarker.user.controller.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.user.constant.UserConsts;
import org.benchmarker.user.model.UserGroup;

@Entity
@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserGroupInfo {
    @Id
    @Builder.Default
    private String id = UserConsts.USER_GROUP_DEFAULT_ID;

    @Column(name = "group_name")
    @Builder.Default
    private String name = UserConsts.USER_GROUP_DEFAULT_NAME;

    public static UserGroupInfo from(UserGroup userGroup) {
        return UserGroupInfo.builder()
            .id(userGroup.getId())
            .name(userGroup.getName())
            .build();
    }
}
