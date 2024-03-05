package org.benchmarker.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.benchmarker.common.model.BaseTime;
import org.benchmarker.user.constant.UserConsts;

@Entity
@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup extends BaseTime {

    @Id
    @Builder.Default
    private String id = UserConsts.USER_GROUP_DEFAULT_ID;

    @Column(name = "group_name")
    @Builder.Default
    private String name = UserConsts.USER_GROUP_DEFAULT_NAME;


}
