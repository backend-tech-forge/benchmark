package org.benchmarker.user.model.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GroupRoleTest {
    @Test
    void isLeader() {
        assertTrue(GroupRole.LEADER.isLeader());
        assertFalse(GroupRole.MEMBER.isLeader());
    }

}