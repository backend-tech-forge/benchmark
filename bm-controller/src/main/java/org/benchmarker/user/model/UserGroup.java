package org.benchmarker.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.benchmarker.common.model.BaseTime;

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


}
