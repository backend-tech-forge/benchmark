package org.benchmarker.bmcontroller.user.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    @DisplayName("Role Enum 에서 Role을 가져온다")
    void fromString() {
        // given
        String role = "USER";
        Role expected = Role.ROLE_USER;

        // when
        Optional<Role> myrole = Role.fromString(role);
        String roles = expected.getRole();
        String description = expected.getDescription();

        // then
        assertThat(myrole).isNotEmpty();
        assertEquals(expected, myrole.get());
        assertThat(roles).isEqualTo("USER");
        assertThat(description).isEqualTo("normal user");
    }
}