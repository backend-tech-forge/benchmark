package org.benchmarker.user.model;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public enum Role {

    ROLE_USER("USER", "normal user"),
    ROLE_ADMIN("ADMIN", "admin user"),
    ;
    private final String role;
    private final String description;

    public static Optional<Role> fromString(String role) {
        for (Role r : Role.values()) {
            if (r.role.equals(role)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isAdmin() {
        if (this.equals(ROLE_ADMIN)) {
            return true;
        }
        return false;
    }
}
