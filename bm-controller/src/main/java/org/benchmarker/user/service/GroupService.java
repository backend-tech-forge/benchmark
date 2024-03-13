package org.benchmarker.user.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.controller.dto.GroupAddDto;
import org.benchmarker.user.controller.dto.GroupInfo;
import org.benchmarker.user.controller.dto.GroupUpdateDto;
import org.benchmarker.user.controller.dto.UserGroupRoleInfo;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;
import org.benchmarker.user.model.enums.GroupRole;
import org.benchmarker.user.repository.UserGroupJoinRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;

    /**
     * Create a group and become a leader of the group
     *
     * @param dto    {@link GroupAddDto}
     * @param userId {@link String}
     * @return {@link GroupInfo}
     */
    @Transactional
    public GroupInfo createGroup(GroupAddDto dto, String userId) {
        groupRepository.findById(dto.getId()).ifPresent((u) -> {
            throw new GlobalException(ErrorCode.GROUP_ALREADY_EXIST);
        });

        UserGroup userGroup = UserGroup.builder()
            .id(dto.getId())
            .name(dto.getName())
            .build();
        UserGroup save = groupRepository.save(userGroup);
        addUserToGroupAdmin(save.getId(), userId, GroupRole.LEADER);

        return GroupInfo.builder()
            .id(save.getId())
            .name(save.getName())
            .users(getUserInfoInGroup(save.getId()))
            .build();
    }

    /**
     * ADMIN ONLY
     *
     * <p>Get group info for admin
     *
     * @param group_id
     * @return {@link GroupInfo}
     */
    @Transactional
    public GroupInfo getGroupInfoAdmin(String group_id) {
        return groupRepository.findById(group_id).map((g) -> GroupInfo.builder()
            .id(g.getId())
            .name(g.getName())
            .users(getUserInfoInGroup(group_id))
            .build()).orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
    }

    /**
     * Get group info for user
     *
     * @param groupId
     * @param userId
     * @return {@link GroupInfo}
     */
    @Transactional
    public GroupInfo getGroupInfo(String groupId, String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));

        // if user is not in the group, throw exception
        if (user.getUserGroupJoin().stream()
            .noneMatch((j) -> j.getUserGroup().getId().equals(groupId))) {
            throw new GlobalException(ErrorCode.USER_NOT_IN_GROUP);
        }
        return groupRepository.findById(groupId).map((g) -> GroupInfo.builder()
            .id(g.getId())
            .name(g.getName())
            .users(getUserInfoInGroup(groupId))
            .build()).orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
    }

    @Transactional
    public List<GroupInfo> getAllGroupInfo(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        List<GroupInfo> groupInfos = user.getUserGroupJoin().stream()
            .map(UserGroupJoin::getUserGroup)
            .map((g) -> GroupInfo.builder()
                .id(g.getId())
                .name(g.getName())
                .users(getUserInfoInGroup(g.getId()))
                .build())
            .map((g) -> GroupInfo.builder()
                .id(g.getId())
                .name(g.getName())
                .users(getUserInfoInGroup(g.getId()))
                .build())
            .collect(Collectors.toList());
        return groupInfos;
    }

    @Transactional
    public Page<GroupInfo> getAllGroupInfo(String userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        List<GroupInfo> groupInfos = user.getUserGroupJoin().stream()
            .map(UserGroupJoin::getUserGroup)
            .map((g) -> GroupInfo.builder()
                .id(g.getId())
                .name(g.getName())
                .users(getUserInfoInGroup(g.getId()))
                .build())
            .collect(Collectors.toList());
        return new PageImpl<>(groupInfos, pageable, groupInfos.size());
    }

    @Transactional
    public List<GroupInfo> getAllGroupInfoAdmin() {
        List<GroupInfo> groupInfos = groupRepository.findAll().stream()
            .map((g) -> GroupInfo.builder()
                .id(g.getId())
                .name(g.getName())
                .users(getUserInfoInGroup(g.getId()))
                .build())
            .collect(Collectors.toList());
        return groupInfos;
    }

    @Transactional
    public Page<GroupInfo> getAllGroupInfoAdmin(Pageable pageable) {
        Page<UserGroup> groupPage = groupRepository.findAll(pageable);
        return groupPage.map(g -> {
            return GroupInfo.builder()
                .id(g.getId())
                .name(g.getName())
                .users(getUserInfoInGroup(g.getId()))
                .build();
        });
    }

    /**
     * ADMIN ONLY
     *
     * <p>Update group name for admin
     *
     * @param dto
     * @param groupId
     * @return {@link GroupInfo}
     */
    @Transactional
    public GroupInfo updateGroupAdmin(GroupUpdateDto dto, String groupId) {
        UserGroup userGroup = groupRepository.findById(groupId).orElseThrow(() ->
            new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        userGroup.setName(dto.getName());
        return GroupInfo.builder()
            .id(userGroup.getId())
            .name(userGroup.getName())
            .users(getUserInfoInGroup(userGroup.getId()))
            .build();
    }

    /**
     * Update group name for user
     *
     * @param dto
     * @param groupId
     * @param userId
     * @return {@link GroupInfo}
     */
    @Transactional
    public GroupInfo updateGroupUser(GroupUpdateDto dto, String groupId, String userId) {
        Optional<UserGroupJoin> findJoin = userGroupJoinRepository.findByUserIdAndUserGroupId(
            userId, groupId);
        if (findJoin.isEmpty()) {
            throw new GlobalException(ErrorCode.USER_NOT_IN_GROUP);
        }
        UserGroup userGroup = groupRepository.findById(groupId).orElseThrow(() ->
            new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        userGroup.setName(dto.getName());

        return GroupInfo.builder()
            .id(userGroup.getId())
            .name(userGroup.getName())
            .users(getUserInfoInGroup(userGroup.getId()))
            .build();
    }

    /**
     * Add user to group
     *
     * @param groupId
     * @param myId
     * @param userId
     * @return {@link GroupInfo}
     */
    @Transactional
    public GroupInfo addUserToGroup(String groupId, String myId, String userId, GroupRole role) {
        userRepository.findById(myId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        // check user is in the group and has LEADER role
        userGroupJoinRepository.findByUserIdAndUserGroupId(myId, groupId).ifPresentOrElse(
            (u) -> {
                if (u.getRole() != GroupRole.LEADER) {
                    throw new GlobalException(ErrorCode.FORBIDDEN);
                }
            },
            () -> {
                throw new GlobalException(ErrorCode.USER_NOT_IN_GROUP);
            });

        // join user to the group
        User toAddUser = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        UserGroup foundGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        Optional<UserGroupJoin> foundJoin = userGroupJoinRepository.findByUserAndUserGroup(
            toAddUser, foundGroup);
        foundJoin.ifPresent((u) -> {
            throw new GlobalException(ErrorCode.USER_ALREADY_IN_GROUP);
        });

        UserGroupJoin userGroupJoin = UserGroupJoin.builder()
            .user(toAddUser)
            .userGroup(foundGroup)
            .role(role)
            .build();
        userGroupJoinRepository.save(userGroupJoin);
        return GroupInfo.builder()
            .id(foundGroup.getId())
            .name(foundGroup.getName())
            .users(getUserInfoInGroup(foundGroup.getId()))
            .build();
    }

    /**
     * ADMIN ONLY
     *
     * <p>Add user to group
     *
     * @param groupId
     * @param userId
     * @return
     */
    @Transactional
    public GroupInfo addUserToGroupAdmin(String groupId, String userId, GroupRole role) {
        User toAddUser = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        UserGroup foundGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        Optional<UserGroupJoin> foundJoin = userGroupJoinRepository.findByUserAndUserGroup(
            toAddUser, foundGroup);
        foundJoin.ifPresent((u) -> {
            throw new GlobalException(ErrorCode.USER_ALREADY_IN_GROUP);
        });

        UserGroupJoin userGroupJoin = UserGroupJoin.builder()
            .user(toAddUser)
            .userGroup(foundGroup)
            .role(role) // default role, can be changed later
            .build();
        userGroupJoinRepository.save(userGroupJoin);
        return GroupInfo.builder()
            .id(foundGroup.getId())
            .name(foundGroup.getName())
            .users(getUserInfoInGroup(foundGroup.getId()))
            .build();
    }

    @Transactional
    public GroupInfo deleteUserFromGroup(String groupId, String myId, String userId,
        boolean isAdmin) {
        if (isAdmin) {
            userGroupJoinRepository.deleteAllByUserIdAAndUserGroupId(userId, groupId);
            UserGroup foundGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
            return GroupInfo.builder()
                .id(groupId)
                .name("deleted")
                .users(getUserInfoInGroup(foundGroup.getId()))
                .build();
        }
        // check if user is in the group and has permission to delete = LEADER
        Optional<UserGroupJoin> foundJoin = userGroupJoinRepository.findByUserIdAndUserGroupId(
            myId, groupId);
        if (foundJoin.isEmpty() || foundJoin.get().getRole() != GroupRole.LEADER) {
            System.out.println("foundJoin: " + foundJoin);
            throw new GlobalException(ErrorCode.FORBIDDEN);
        }
        UserGroupJoin extractUser = userGroupJoinRepository.findByUserIdAndUserGroupId(userId, groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_IN_GROUP));
        
        userGroupJoinRepository.delete(extractUser);
        UserGroup foundGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        return GroupInfo.builder()
            .id(foundGroup.getId())
            .name(foundGroup.getName())
            .users(getUserInfoInGroup(foundGroup.getId()))
            .build();
    }

    @Transactional
    public void deleteGroup(String groupId, String myId) {
        UserGroup foundGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        UserGroupJoin join = userGroupJoinRepository.findByUserIdAndUserGroupId(
            myId, groupId).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_IN_GROUP));

        if (!join.getRole().isLeader()){
            throw new GlobalException(ErrorCode.FORBIDDEN);
        }
        userGroupJoinRepository.deleteAllByUserGroupId(groupId);
        groupRepository.delete(foundGroup);
    }

    @Transactional
    public void deleteGroupAdmin(String groupId) {
        UserGroup foundGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        userGroupJoinRepository.deleteAllByUserGroupId(groupId);
        groupRepository.delete(foundGroup);
    }

    private List<UserGroupRoleInfo> getUserInfoInGroup(String groupId) {
        return userGroupJoinRepository.findByUserGroupId(groupId).stream()
            .map((userGroupJoin -> {
                User user = userGroupJoin.getUser();
                return UserGroupRoleInfo.builder()
                    .id(user.getId())
                    .role(userGroupJoin.getRole())
                    .build();
            })).collect(Collectors.toList());
    }

}
