package org.benchmarker.bmcontroller.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.benchmarker.bmcontroller.common.model.BaseTime;
import org.benchmarker.bmcontroller.user.constant.UserConsts;

@Entity
@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserGroup extends BaseTime {

    @Id
    @Builder.Default
    private String id = UserConsts.USER_GROUP_DEFAULT_ID;

    @Column(name = "group_name")
    @Builder.Default
    private String name = UserConsts.USER_GROUP_DEFAULT_NAME;

    public void update(String userGroupName) {
        this.id = userGroupName;
    }

}

