package org.benchmarker.bmcontroller.user.model.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.benchmarker.bmcontroller.user.model.enums.GroupRole;
import org.junit.jupiter.api.Test;

class GroupRoleTest {
    @Test
    void isLeader() {
        assertTrue(GroupRole.LEADER.isLeader());
        assertFalse(GroupRole.MEMBER.isLeader());
    }

}