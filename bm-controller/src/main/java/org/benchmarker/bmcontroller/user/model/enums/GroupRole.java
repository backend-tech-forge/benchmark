package org.benchmarker.bmcontroller.user.model.enums;

/**
 * role of the user in the group {@link #LEADER} or {@link #MEMBER}
 */
public enum GroupRole {
    /**
     * Leader of the group
     */
    LEADER,
    /**
     * Member of the group
     */
    MEMBER;

    public Boolean isLeader() {
        return this.equals(LEADER);
    }
}
