package org.benchmarker.user.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.controller.dto.UserUpdateDto;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.model.UserGroupJoin;
import org.benchmarker.user.model.enums.GroupRole;
import org.benchmarker.user.repository.UserGroupJoinRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.benchmarker.common.util.NoOp.noOp;
import static org.benchmarker.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;
import static org.benchmarker.user.constant.UserConsts.USER_GROUP_DEFAULT_NAME;

@Service("userService")
@RequiredArgsConstructor
@Slf4j
public class UserService extends AbstractUserService {

    private final UserRepository userRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository userGroupRepository;

    @Override
    @Transactional
    public Optional<UserInfo> createUser(UserRegisterDto req) {
        userRepository.findById(req.getId()).ifPresent((u) -> {
            throw new GlobalException(ErrorCode.USER_ALREADY_EXIST);
        });
        // if userGroup is empty, save user with default userGroup
        if (req.getUserGroup() == null || req.getUserGroup().isEmpty()) {
            Optional<UserGroup> defaultGroup = userGroupRepository.findById(USER_GROUP_DEFAULT_ID)
                .or(() -> Optional.of(userGroupRepository.save(UserGroup.builder()
                    .id(USER_GROUP_DEFAULT_ID)
                    .name(USER_GROUP_DEFAULT_NAME)
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
        ArrayList<UserGroupJoin> userGroupJoins = new ArrayList<>();
        req.getUserGroup().forEach(group -> {
            UserGroup userGroup = userGroupRepository.findById(group.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
            // find UserAndGroups check if userGroupJoin already exists, do nothing
            // if userGroupJoin does not exist, save userGroupJoin
            Optional<UserGroupJoin> findJoins = userGroupJoinRepository.findByUserAndUserGroup(
                user, userGroup);
            if (findJoins.isEmpty()) {
                UserGroupJoin saved = userGroupJoinRepository.save(UserGroupJoin.builder()
                    .user(user)
                    .userGroup(userGroup)
                    .build());
                userGroupJoins.add(saved);
            } else {
                userGroupJoins.add(findJoins.get());
                noOp();
            }
        });
        user.setEmail(req.getEmail());
        user.setEmailNotification(req.getEmailNotification());
        user.setSlackWebhookUrl(req.getSlackWebhookUrl());
        user.setSlackNotification(req.getSlackNotification());
        user.setUserGroupJoin(userGroupJoins);
        User save = userRepository.save(user);

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
