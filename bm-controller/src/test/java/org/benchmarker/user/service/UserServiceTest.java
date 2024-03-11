package org.benchmarker.user.service;

import java.util.List;
import org.benchmarker.user.controller.constant.TestUserConsts;
import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.controller.dto.UserUpdateDto;
import org.util.initialize.InitiClass;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.model.UserGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends InitiClass {

    @Test
    @DisplayName("사용자를 생성하면 사용자 정보를 반환한다")
    public void createUser() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id(TestUserConsts.groupId)
            .name(TestUserConsts.groupName)
            .build();
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of(userGroup))
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();

        // When
        Optional<UserInfo> userInfo = userService.createUser(userRegisterDto);

        // Then
        assertThat(userInfo).isNotEmpty();
        UserInfo u = userInfo.get();
        assertThat(u.getId()).isEqualTo(userRegisterDto.getId());
        assertThat(u.getEmail()).isEqualTo(userRegisterDto.getEmail());
        assertThat(u.getSlackWebhookUrl()).isEqualTo(userRegisterDto.getSlackWebhookUrl());
        assertThat(u.getSlackNotification()).isEqualTo(userRegisterDto.getSlackNotification());
        assertThat(u.getEmailNotification()).isEqualTo(userRegisterDto.getEmailNotification());
        assertThat(u.getUserGroup()).isEqualTo(userRegisterDto.getUserGroup());
    }

    @Test
    @DisplayName("사용자 group 업데이트 시 group 이 존재하지 않는다면 GlobalException 에러를 반환한다")
    public void updateUser() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id(TestUserConsts.groupId)
            .name(TestUserConsts.groupName)
            .build();
        UserGroup otherGroup = UserGroup.builder()
            .id("otherGroupId")
            .name("otherGroupName")
            .build();
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of(userGroup))
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of(userGroup, otherGroup)) // add other group
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();
        userService.createUser(userRegisterDto);

        // When
        ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
            userService.updateUser(userUpdateDto);
        }).getErrorCode();

        // Then
        assertThat(errorCode).isEqualTo(ErrorCode.GROUP_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 group 업데이트 시 group 이 존재한다면 유저 정보를 반환한다")
    public void updateUserSuccess() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id(TestUserConsts.groupId)
            .name(TestUserConsts.groupName)
            .build();
        UserGroup otherGroup = UserGroup.builder()
            .id("otherGroupId")
            .name("otherGroupName")
            .build();
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of(userGroup))
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of(userGroup, otherGroup)) // add other group
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();

        userGroupRepository.save(otherGroup); // save other group before update user
        userService.createUser(userRegisterDto);

        // When
        Optional<UserInfo> userInfo = userService.updateUser(userUpdateDto);

        // Then
        assertThat(userInfo).isNotEmpty();
        UserInfo u = userInfo.get();
        assertThat(u.getId()).isEqualTo(userUpdateDto.getId());
        assertThat(u.getEmail()).isEqualTo(userUpdateDto.getEmail());
        assertThat(u.getSlackWebhookUrl()).isEqualTo(userUpdateDto.getSlackWebhookUrl());
        assertThat(u.getSlackNotification()).isEqualTo(userUpdateDto.getSlackNotification());
        assertThat(u.getEmailNotification()).isEqualTo(userUpdateDto.getEmailNotification());
        assertThat(u.getUserGroup().get(0).getId()).isEqualTo(userUpdateDto.getUserGroup().get(0).getId());
        u.getUserGroup().stream().forEach(g -> {
            System.out.println(g.getId());
        });
    }

    @Test
    @DisplayName("Group 없는 사용자 생성 시 defaultGroup 으로 가입시킨다")
    public void createUserWithNoGroup() {
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of())
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();

        // When
        Optional<UserInfo> userInfo = userService.createUser(userRegisterDto);

        // Then
        assertThat(userInfo).isNotEmpty();
        UserInfo u = userInfo.get();
        assertThat(u.getId()).isEqualTo(userRegisterDto.getId());
        assertThat(u.getEmail()).isEqualTo(userRegisterDto.getEmail());
        assertThat(u.getSlackWebhookUrl()).isEqualTo(userRegisterDto.getSlackWebhookUrl());
        assertThat(u.getSlackNotification()).isEqualTo(userRegisterDto.getSlackNotification());
        assertThat(u.getEmailNotification()).isEqualTo(userRegisterDto.getEmailNotification());
        assertThat(u.getUserGroup().get(0).getId()).isEqualTo("default");
    }

    @Test
    @DisplayName("사용자를 생성하고 조회하면 사용자 정보를 반환한다")
    public void createUserAndGetUser() {
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of())
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();
        userService.createUser(userRegisterDto);

        // When
        Optional<UserInfo> findUser = userService.getUser(userRegisterDto.getId());

        // Then
        assertThat(findUser).isNotEmpty();
        UserInfo u = findUser.get();
        assertThat(u.getId()).isEqualTo(userRegisterDto.getId());
        assertThat(u.getEmail()).isEqualTo(userRegisterDto.getEmail());
        assertThat(u.getSlackWebhookUrl()).isEqualTo(userRegisterDto.getSlackWebhookUrl());
        assertThat(u.getSlackNotification()).isEqualTo(userRegisterDto.getSlackNotification());
        assertThat(u.getEmailNotification()).isEqualTo(userRegisterDto.getEmailNotification());
        assertThat(u.getUserGroup().get(0).getId()).isEqualTo("default");
    }

    @Test
    @DisplayName("다른 유저를 조회할 때 같은 그룹에 속해있으면 유저정보를 반환한다")
    public void grouptest() {
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of()) // default group
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();

        UserRegisterDto otherUserRegisterDto = UserRegisterDto.builder()
            .id("otherUser")
            .password("password")
            .userGroup(List.of()) // default group
            .email("otherUserEmail")
            .slackWebhookUrl("otherUserSlackWebhookUrl")
            .slackNotification(false)
            .emailNotification(false)
            .build();

        userService.createUser(userRegisterDto);
        userService.createUser(otherUserRegisterDto);

        // When
        UserInfo findUser = userService.getUserIfSameGroup(userRegisterDto.getId(),
            otherUserRegisterDto.getId());

        // Then
//        assertThat(findUser).isNotEmpty();
        assertThat(findUser.getId()).isEqualTo(otherUserRegisterDto.getId());
        assertThat(findUser.getEmail()).isEqualTo(otherUserRegisterDto.getEmail());
        assertThat(findUser.getSlackWebhookUrl()).isEqualTo(
            otherUserRegisterDto.getSlackWebhookUrl());
        assertThat(findUser.getSlackNotification()).isEqualTo(
            otherUserRegisterDto.getSlackNotification());
        assertThat(findUser.getEmailNotification()).isEqualTo(
            otherUserRegisterDto.getEmailNotification());
        assertThat(findUser.getUserGroup().get(0).getId()).isEqualTo("default");
    }

    @Test
    @DisplayName("다른 유저를 조회할 때 같은 그룹에 속해있지 않으면 USER_NOT_SAME_GROUP 에러를 반환한다")
    public void grouptestButError() {
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of()) // default group
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();

        UserRegisterDto otherUserRegisterDto = UserRegisterDto.builder()
            .id("otherUser")
            .password("password")
            .userGroup(List.of(UserGroup.builder()
                .id("otherGroupId")
                .name("otherGroupName")
                .build()))
            .email("otherUserEmail")
            .slackWebhookUrl("otherUserSlackWebhookUrl")
            .slackNotification(false)
            .emailNotification(false)
            .build();

        userService.createUser(userRegisterDto);
        userService.createUser(otherUserRegisterDto);

        // when
        ErrorCode errorCode = assertThrows(GlobalException.class, () -> {
            userService.getUserIfSameGroup(userRegisterDto.getId(),
                otherUserRegisterDto.getId());
        }).getErrorCode();

        // then
        assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_SAME_GROUP);
    }

    @Test
    @DisplayName("중복 id 사용자를 생성하면 USER_ALREADY_EXIST 예외를 반환한다")
    public void createUserException() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id(TestUserConsts.groupId)
            .name(TestUserConsts.groupName)
            .build();
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .id(TestUserConsts.id)
            .password(TestUserConsts.password)
            .userGroup(List.of(userGroup))
            .email(TestUserConsts.email)
            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
            .slackNotification(TestUserConsts.slackNotification)
            .emailNotification(TestUserConsts.emailNotification)
            .build();

        // When
        Optional<UserInfo> userInfo = userService.createUser(userRegisterDto);

        // When & Then
        GlobalException ex = assertThrows(GlobalException.class,
            () -> userService.createUser(userRegisterDto));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_ALREADY_EXIST);
    }

//    @Test
//    @DisplayName("사용자 정보를 업데이트하면 업데이트된 사용자 정보를 반환한다")
//    public void updateUser() throws Exception {
//        // Given
//        UserGroup userGroup = UserGroup.builder()
//            .id(TestUserConsts.groupId)
//            .name(TestUserConsts.groupName)
//            .build();
//        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
//            .id(TestUserConsts.id)
//            .password(TestUserConsts.password)
//            .userGroup(List.of(userGroup))
//            .email(TestUserConsts.email)
//            .slackWebhookUrl(TestUserConsts.slackWebhookUrl)
//            .slackNotification(TestUserConsts.slackNotification)
//            .emailNotification(TestUserConsts.emailNotification)
//            .build();
//        Optional<UserInfo> userInfo = userService.createUser(userRegisterDto);
//
//        // When
//        user.setPassword("newPassword");
//        Optional<User> updatedUser = userService.updateUser(user);
//
//        // Then
//        assertThat(updatedUser).isNotEmpty();
//        User savedUser = updatedUser.get();
//        assertThat(savedUser).isNotNull();
//        assertThat(savedUser.getId()).isEqualTo(user.getId());
//        assertThat(savedUser.getPassword()).isEqualTo("newPassword");
//    }
//
//    @Test
//    @DisplayName("사용자 정보 조회 시 사용자 정보를 반환한다")
//    public void getUser() throws Exception {
//        // Given
//        UserGroup userGroup = UserGroup.builder()
//            .id("default")
//            .name("default")
//            .build();
//        User user = User.builder()
//            .id("test3")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//        userGroupRepository.save(userGroup);
//        userRepository.save(user);
//
//        // when
//        User findUser = userService.getUser(user.getId());
//
//        // then
//        assertThat(findUser).isNotNull();
//        assertThat(findUser.getId()).isEqualTo(user.getId());
//    }
//
//    @Test
//    @DisplayName("Group 내 사용자 정보 조회 시 사용자 정보를 반환한다")
//    public void getUserInSameGroup() throws Exception {
//        // given
//        UserGroup userGroup = UserGroup.builder()
//            .id("default")
//            .name("default")
//            .build();
//        User user = User.builder()
//            .id("test")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//        User otherUser = User.builder()
//            .id("otherUser")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//
//        userGroupRepository.save(userGroup);
//        userRepository.save(user);
//        userRepository.save(otherUser);
//
//        // when
//        User findUser = userService.getUser(otherUser.getId());
//
//        // then
//        assertThat(findUser).isNotNull();
//        assertThat(findUser.getId()).isEqualTo(otherUser.getId());
//        assertThat(findUser.getUserGroup()).isEqualTo(userGroup);
//    }
//
//    @Test
//    @DisplayName("사용자 정보 조회 시 group 이 다르면 GlobalException 에러를 반환한다")
//    public void getUserInNotSameGroup_ThrowException() throws Exception {
//        // given
//        UserGroup userGroup = UserGroup.builder()
//            .id("default")
//            .name("default")
//            .build();
//        User user = User.builder()
//            .id("test")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//        UserGroup otherGroup = UserGroup.builder()
//            .id("otherGroup")
//            .name("default")
//            .build();
//        User otherUser = User.builder()
//            .id("otherUser")
//            .password("password")
//            .userGroup(List.of(otherGroup))
//            .build();
//        userGroupRepository.save(userGroup);
//        userRepository.save(user);
//        userGroupRepository.save(otherGroup);
//        userRepository.save(otherUser);
//
//        // when
//        ErrorCode errorCode = assertThrows((GlobalException.class),
//            () -> userService.getUserIfSameGroup(user.getId(),otherUser.getId())).getErrorCode();
//
//        // then
//        assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_SAME_GROUP);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 사용자 정보를 업데이트하면 예외를 발생시킨다")
//    public void updateUserNonExistingUser() {
//        // Given
//        UserGroup userGroup = UserGroup.builder()
//            .id("default")
//            .name("default")
//            .build();
//        User user = User.builder()
//            .id("test")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//        userGroupRepository.save(userGroup);
//        userRepository.save(user);
//
//        User nonExistUser = User.builder()
//            .id("nonExistingUser")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//
//        // When & Then
//        assertThrows(GlobalException.class,
//            () -> userService.updateUser(nonExistUser));
//    }
//
//    @Test
//    @DisplayName("사용자를 삭제하면 사용자가 삭제된다")
//    public void deleteUser() {
//        // Given
//        UserGroup userGroup = UserGroup.builder()
//            .id("default")
//            .name("default")
//            .build();
//        User user = User.builder()
//            .id("test")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//
//        userService.createUser(user);
//
//        // When
//        userService.deleteUser(user.getId());
//
//        // Then
//        assertThat(userRepository.findById(user.getId())).isEmpty();
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 사용자를 삭제하면 USER_NOT_FOUND 를 반환한다")
//    public void deleteUserException() {
//        // Given
//        UserGroup userGroup = UserGroup.builder()
//            .id("default")
//            .name("default")
//            .build();
//        User user = User.builder()
//            .id("test")
//            .password("password")
//            .userGroup(List.of(userGroup))
//            .build();
//        userService.createUser(user);
//
//        // When
//        GlobalException ex = assertThrows(GlobalException.class,
//            () -> userService.deleteUser("nonExistingUser"));
//
//        // Then
//        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
//    }


}