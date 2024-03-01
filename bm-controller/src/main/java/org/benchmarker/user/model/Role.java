package org.benchmarker.user.model;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public enum Role {

    USER("USER","normal user"),
    ADMIN("ADMIN","admin user"),
    ;
    private final String role;
    private final String description;

    public Optional<Role> fromString(String role) {
        for (Role r : Role.values()) {
            if (r.role.equals(role)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }
}
