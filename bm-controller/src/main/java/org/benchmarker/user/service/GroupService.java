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
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;
import org.benchmarker.user.model.enums.GroupRole;
import org.benchmarker.user.repository.UserGroupJoinRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;

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
        return GroupInfo.builder()
            .id(save.getId())
            .name(save.getName())
            .users(List.of(userId))
            .build();
    }

    @Transactional
    public GroupInfo getGroupInfoAdmin(String group_id) {
        return groupRepository.findById(group_id).map((g) -> GroupInfo.builder()
            .id(g.getId())
            .name(g.getName())
            .users(getUserIdsInGroup(group_id))
            .build()).orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
    }

    @Transactional
    public GroupInfo getGroupInfo(String groupId, String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        // if user is not in the group, throw exception
        if (user.getUserGroupJoin().stream()
            .noneMatch((j) -> j.getUserGroup().getId().equals(groupId))) {
            throw new GlobalException(ErrorCode.USER_NOT_IN_GROUP);
        }
        return groupRepository.findById(groupId).map((g) -> GroupInfo.builder()
            .id(g.getId())
            .name(g.getName())
            .users(getUserIdsInGroup(groupId))
            .build()).orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
    }

    @Transactional
    public GroupInfo updateGroupA(GroupUpdateDto dto, String groupId) {
        UserGroup userGroup = groupRepository.findById(groupId).orElseThrow(() ->
            new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        userGroup.setName(dto.getName());
        return GroupInfo.builder()
            .id(userGroup.getId())
            .name(userGroup.getName())
            .users(getUserIdsInGroup(userGroup.getId()))
            .build();
    }

    @Transactional
    public GroupInfo updateGroupU(GroupUpdateDto dto, String groupId, String userId) {
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
            .users(getUserIdsInGroup(userGroup.getId()))
            .build();
    }

    @Transactional
    public GroupInfo addUserToGroup(String groupId, String myId, String userId) {
        userRepository.findById(myId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
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
            .role(GroupRole.MEMBER)
            .build();
        userGroupJoinRepository.save(userGroupJoin);
        return GroupInfo.builder()
            .id(foundGroup.getId())
            .name(foundGroup.getName())
            .users(getUserIdsInGroup(foundGroup.getId()))
            .build();
    }

    @Transactional
    public GroupInfo addUserToGroupAdmin(String groupId, String userId) {
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
            .role(GroupRole.MEMBER) // default role, can be changed later
            .build();
        userGroupJoinRepository.save(userGroupJoin);
        return GroupInfo.builder()
            .id(foundGroup.getId())
            .name(foundGroup.getName())
            .users(getUserIdsInGroup(foundGroup.getId()))
            .build();
    }

    private List<String> getUserIdsInGroup(String groupId) {
        return userGroupJoinRepository.findByUserGroupId(groupId).stream()
            .map(UserGroupJoin::getUser).map(User::getId).collect(Collectors.toList());
    }

    public GroupInfo deleteUserFromGroup(String groupId, String myId, String userId, boolean isAdmin) {
        if (isAdmin) {
            userGroupJoinRepository.deleteByUserId(userId);
            UserGroup foundGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
            return GroupInfo.builder()
                .id(foundGroup.getId())
                .name(foundGroup.getName())
                .users(getUserIdsInGroup(foundGroup.getId()))
                .build();
        }
        // check if user is in the group and has permission to delete = LEADER
        Optional<UserGroupJoin> foundJoin = userGroupJoinRepository.findByUserIdAndUserGroupId(
            myId, groupId);
        if (foundJoin.isEmpty() || foundJoin.get().getRole() != GroupRole.LEADER) {
            throw new GlobalException(ErrorCode.FORBIDDEN);
        }
        userGroupJoinRepository.deleteByUserId(userId);
        UserGroup foundGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        return GroupInfo.builder()
            .id(foundGroup.getId())
            .name(foundGroup.getName())
            .users(getUserIdsInGroup(foundGroup.getId()))
            .build();
    }
}
