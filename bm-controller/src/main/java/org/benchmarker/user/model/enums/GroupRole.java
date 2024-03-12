package org.benchmarker.user.model.enums;

public enum GroupRole {
    LEADER,
    MEMBER;

    public Boolean isLeader() {
        return this.equals(LEADER);
    }
}
