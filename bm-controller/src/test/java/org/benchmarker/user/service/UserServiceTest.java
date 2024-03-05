package org.benchmarker.user.service;

import org.util.initialize.InitiClass;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.model.User;
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
        User user = User.builder()
            .id("test")
            .password("password")
            .build();
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        userGroupRepository.save(userGroup);
        UserGroup defaultUserGroup = userGroupRepository.findById("default").get();
        user.setUserGroup(defaultUserGroup);

        // When
        Optional<User> createdUser = userService.createUser(user);

        // Then
        assertThat(createdUser).isNotEmpty();
        User savedUser = createdUser.get();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(user.getId());
        assertThat(savedUser.getUserGroup()).isEqualTo(defaultUserGroup);
    }

    @Test
    @DisplayName("중복 id 사용자를 생성하면 USER_ALREADY_EXIST 예외를 반환한다")
    public void createUserException() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test")
            .password("password")
            .userGroup(userGroup)
            .build();
        userGroupRepository.save(userGroup);
        userService.createUser(user);

        User dupUser = User.builder()
            .id("test")
            .userGroup(userGroup)
            .build();

        // When & Then
        GlobalException ex = assertThrows(GlobalException.class,
            () -> userService.createUser(dupUser));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_ALREADY_EXIST);
    }

    @Test
    @DisplayName("사용자 정보를 업데이트하면 업데이트된 사용자 정보를 반환한다")
    public void updateUser() throws Exception {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test3")
            .password("password")
            .userGroup(userGroup)
            .build();
        userGroupRepository.save(userGroup);
        userRepository.save(user);

        // When
        user.setPassword("newPassword");
        Optional<User> updatedUser = userService.updateUser(user);

        // Then
        assertThat(updatedUser).isNotEmpty();
        User savedUser = updatedUser.get();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(user.getId());
        assertThat(savedUser.getPassword()).isEqualTo("newPassword");
    }

    @Test
    @DisplayName("사용자 정보 조회 시 사용자 정보를 반환한다")
    public void getUser() throws Exception {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test3")
            .password("password")
            .userGroup(userGroup)
            .build();
        userGroupRepository.save(userGroup);
        userRepository.save(user);

        // when
        User findUser = userService.getUser(user.getId());

        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Group 내 사용자 정보 조회 시 사용자 정보를 반환한다")
    public void getUserInSameGroup() throws Exception {
        // given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test")
            .password("password")
            .userGroup(userGroup)
            .build();
        User otherUser = User.builder()
            .id("otherUser")
            .password("password")
            .userGroup(userGroup)
            .build();
        userGroupRepository.save(userGroup);
        userRepository.save(user);
        userRepository.save(otherUser);

        // when
        User findUser = userService.getUser(otherUser.getId());

        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isEqualTo(otherUser.getId());
        assertThat(findUser.getUserGroup()).isEqualTo(userGroup);
    }

    @Test
    @DisplayName("사용자 정보 조회 시 group 이 다르면 GlobalException 에러를 반환한다")
    public void getUserInNotSameGroup_ThrowException() throws Exception {
        // given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test")
            .password("password")
            .userGroup(userGroup)
            .build();
        UserGroup otherGroup = UserGroup.builder()
            .id("otherGroup")
            .name("default")
            .build();
        User otherUser = User.builder()
            .id("otherUser")
            .password("password")
            .userGroup(otherGroup)
            .build();
        userGroupRepository.save(userGroup);
        userRepository.save(user);
        userGroupRepository.save(otherGroup);
        userRepository.save(otherUser);

        // when
        ErrorCode errorCode = assertThrows((GlobalException.class),
            () -> userService.getUserIfSameGroup(user.getId(),otherUser.getId())).getErrorCode();

        // then
        assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_SAME_GROUP);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 정보를 업데이트하면 예외를 발생시킨다")
    public void updateUserNonExistingUser() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test")
            .password("password")
            .userGroup(userGroup)
            .build();
        userGroupRepository.save(userGroup);
        userRepository.save(user);

        User nonExistUser = User.builder()
            .id("nonExistingUser")
            .password("password")
            .userGroup(userGroup)
            .build();

        // When & Then
        assertThrows(GlobalException.class,
            () -> userService.updateUser(nonExistUser));
    }

    @Test
    @DisplayName("사용자를 삭제하면 사용자가 삭제된다")
    public void deleteUser() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test")
            .password("password")
            .userGroup(userGroup)
            .build();
        userGroupRepository.save(userGroup);
        userService.createUser(user);

        // When
        userService.deleteUser(user.getId());

        // Then
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 삭제하면 USER_NOT_FOUND 를 반환한다")
    public void deleteUserException() {
        // Given
        UserGroup userGroup = UserGroup.builder()
            .id("default")
            .name("default")
            .build();
        User user = User.builder()
            .id("test")
            .password("password")
            .userGroup(userGroup)
            .build();
        userGroupRepository.save(userGroup);
        userService.createUser(user);

        // When
        GlobalException ex = assertThrows(GlobalException.class,
            () -> userService.deleteUser("nonExistingUser"));

        // Then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }


}