package org.benchmarker.bmcontroller.user.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.user.controller.dto.GroupAddDto;
import org.benchmarker.bmcontroller.user.controller.dto.UserGroupRoleInfo;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.benchmarker.bmcontroller.user.util.UserServiceUtils;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.service.ITestTemplateService;
import org.benchmarker.bmcontroller.user.controller.dto.GroupInfo;
import org.benchmarker.bmcontroller.user.controller.dto.GroupUpdateDto;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.model.enums.GroupRole;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * {@link GroupService} is a service that can manage user groups
 */
@Service
@RequiredArgsConstructor
public class GroupService {

    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;
    private final ITestTemplateService testTemplateService;
    private final UserServiceUtils userServiceUtils;

    /**
     * Create a group and become a {@link GroupRole#LEADER} of the group
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
     * <strong>ADMIN ONLY</strong>
     *
     * <p>Get group info for admin
     *
     * @param groupId
     * @return {@link GroupInfo}
     */
    @Transactional
    public GroupInfo getGroupInfoAdmin(String groupId) {

        List<TestTemplateResponseDto> templates = testTemplateService.getTemplates(groupId);

        return groupRepository.findById(groupId).map((g) -> GroupInfo.builder()
            .id(g.getId())
            .name(g.getName())
            .users(getUserInfoInGroup(groupId))
            .templates(templates)
            .build()).orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
    }

    @Transactional
    public GroupInfo getGroupInfo(String groupId, String userId) {
        return userServiceUtils.getGroupInfo(groupId, userId);
    }

    /**
     * Get all participate {@link GroupInfo} of user
     *
     * @param userId
     * @return {@link List} of {@link GroupInfo}
     */
    @Transactional
    public List<GroupInfo> getAllGroupInfo(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        return user.getUserGroupJoin().stream()
            .map(UserGroupJoin::getUserGroup)
            .map((g) -> GroupInfo.builder()
                .id(g.getId())
                .name(g.getName())
                .users(getUserInfoInGroup(g.getId()))
                .build())
            .collect(Collectors.toList());
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

    /**
     * <strong>ADMIN ONLY</strong>
     *
     * <p>Get all group info for admin with pageable
     *
     * @param pageable
     * @return {@link Page} of {@link GroupInfo}
     */
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
     * <strong>ADMIN ONLY</strong>
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
     * @param role    {@link GroupRole}
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
     * <strong>ADMIN ONLY</strong>
     *
     * <p>Add user to group
     *
     * @param groupId
     * @param userId
     * @return {@link GroupInfo}
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

    /**
     * Delete user from group
     *
     * @param groupId
     * @param myId
     * @param userId
     * @param isAdmin
     * @return {@link GroupInfo}
     */
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
        UserGroupJoin extractUser = userGroupJoinRepository.findByUserIdAndUserGroupId(userId,
                groupId)
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

        if (!join.getRole().isLeader()) {
            throw new GlobalException(ErrorCode.FORBIDDEN);
        }
        userGroupJoinRepository.deleteAllByUserGroupId(groupId);
        groupRepository.delete(foundGroup);
    }

    /**
     * <strong>ADMIN ONLY</strong>
     * <p>Delete group and participate info in the group
     *
     * @param groupId
     */
    @Transactional
    public void deleteGroupAdmin(String groupId) {
        UserGroup foundGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        userGroupJoinRepository.deleteAllByUserGroupId(groupId);
        groupRepository.delete(foundGroup);
    }

    /**
     * Get all {@link UserGroupRoleInfo} in the group
     *
     * @param groupId
     * @return {@link List} of {@link UserGroupRoleInfo}
     */
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
