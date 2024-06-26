package org.benchmarker.bmcontroller.user.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.user.constant.UserConsts;
import org.benchmarker.bmcontroller.user.controller.dto.UserInfo;
import org.benchmarker.bmcontroller.user.controller.dto.UserRegisterDto;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.benchmarker.bmcontroller.user.util.UserServiceUtils;
import org.benchmarker.bmcontroller.user.controller.dto.UserUpdateDto;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.model.enums.GroupRole;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("userService")
@RequiredArgsConstructor
@Slf4j
public class UserService extends AbstractUserService {

    private final UserRepository userRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository userGroupRepository;
    private final UserServiceUtils userServiceUtils;

    @Override
    @Transactional
    public Optional<UserInfo> createUser(UserRegisterDto req) {
        userRepository.findById(req.getId()).ifPresent((u) -> {
            throw new GlobalException(ErrorCode.USER_ALREADY_EXIST);
        });
        // if userGroup is empty, save user with default userGroup
        if (req.getUserGroup() == null || req.getUserGroup().isEmpty()) {
            Optional<UserGroup> defaultGroup = userGroupRepository.findById(
                    UserConsts.USER_GROUP_DEFAULT_ID)
                .or(() -> Optional.of(userGroupRepository.save(UserGroup.builder()
                    .id(UserConsts.USER_GROUP_DEFAULT_ID)
                    .name(UserConsts.USER_GROUP_DEFAULT_NAME)
                    .build())));

            User saveUser = req.toEntity();
            saveUser.setPassword(passwordEncoder.encode(saveUser.getPassword()));
            User save = userRepository.save(saveUser);
            userGroupJoinRepository.save(UserGroupJoin.builder()
                .user(save)
                .userGroup(defaultGroup.get())
                .build());
            return Optional.of(UserInfo.builder()
                .id(saveUser.getId())
                .slackWebhookUrl(saveUser.getSlackWebhookUrl())
                .slackNotification(saveUser.getSlackNotification())
                .email(saveUser.getEmail())
                .emailNotification(saveUser.getEmailNotification())
                .userGroup(Collections.singletonList(defaultGroup.get()))
                .build());
        } else {
            // if userGroup is not empty, save user with userGroup as LEADER
            List<UserGroup> userGroups = new ArrayList<>();
            req.getUserGroup().forEach(group ->
                {
                    userGroupRepository.findById(group.getId()).ifPresentOrElse(
                        (g) -> {
                            throw new GlobalException(ErrorCode.GROUP_ALREADY_EXIST);
                        },
                        () -> {
                            UserGroup savedGroup = userGroupRepository.save(group);
                            userGroups.add(savedGroup);
                        }
                    );
                }
            );
            User saveUser = req.toEntity();
            saveUser.setPassword(passwordEncoder.encode(saveUser.getPassword()));
            User save = userRepository.save(saveUser);
            userGroups.forEach(group ->
                {
                    userGroupJoinRepository.save(UserGroupJoin.builder()
                        .user(save)
                        .userGroup(group)
                        .role(GroupRole.LEADER)
                        .build());
                }
            );
            return Optional.of(UserInfo.builder()
                .id(saveUser.getId())
                .slackWebhookUrl(saveUser.getSlackWebhookUrl())
                .slackNotification(saveUser.getSlackNotification())
                .email(saveUser.getEmail())
                .emailNotification(saveUser.getEmailNotification())
                .userGroup(req.getUserGroup())
                .build());
        }
    }

    /**
     * Get user by id
     * <p>if user not found, throw exception
     *
     * @param id
     * @return {@link Optional} of {@link UserInfo}
     */
    @Override
    @Transactional
    public Optional<UserInfo> getUser(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        return Optional.of(UserInfo.from(user));
    }

    /**
     * Get user by id if user is in the same group as current user
     * <p>if user not found or not same group, throw exception
     *
     * @param currentUserId
     * @param id
     * @return {@link UserInfo}
     */
    @Override
    @Transactional
    public UserInfo getUserIfSameGroup(String currentUserId, String id) {
        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        User user = userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        List<UserGroupJoin> currentUserGroupJoin = currentUser.getUserGroupJoin();
        List<UserGroupJoin> userGroupJoin = user.getUserGroupJoin();

        // if currentUserGroupJoin has userGroup that userGroupJoin has, return user
        // else throw exception
        if (currentUserGroupJoin.stream().anyMatch(
            currentUserGroup -> userGroupJoin.stream().anyMatch(
                userGroup -> userGroup.getUserGroup().getId()
                    .equals(currentUserGroup.getUserGroup().getId())
            )
        )) {
            return UserInfo.from(user);
        } else {
            throw new GlobalException(ErrorCode.USER_NOT_SAME_GROUP);
        }
    }

    @Transactional
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<UserInfo> updateUser(UserUpdateDto req) {
        User user = userRepository.findById(req.getId())
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        User save = userServiceUtils.updateUser(user, req);
        UserInfo info = UserInfo.from(save);
        return Optional.of(info);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        userGroupJoinRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

}
