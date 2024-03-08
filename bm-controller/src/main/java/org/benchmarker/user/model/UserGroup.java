package org.benchmarker.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.benchmarker.common.model.BaseTime;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class UserGroup extends BaseTime {

    @Id
    private String id;
    @Column(name = "group_name")

    private String name;

    @Builder
    public UserGroup(String id, String name) {
        this.id = id;
        this.name = name;

        if (this.name == null) {
            this.name = "default";
        }
        if (this.id == null) {
            this.id = "default";
        }
    }

    public void update(String userGroupName) {
        this.id = userGroupName;
        this.updatedAt = LocalDateTime.now();
    }
}
