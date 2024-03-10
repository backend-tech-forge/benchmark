package org.benchmarker.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class GroupAddDto {
    // group id
    @JsonProperty("id")
    private String id;
    // group name
    @JsonProperty("name")
    private String name;

}
