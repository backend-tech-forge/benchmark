package org.benchmarker.bmcontroller.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.template.repository.TestMttfbRepository;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.repository.TestTpsRepository;
import org.benchmarker.bmcontroller.user.controller.dto.GroupAddDto;
import org.benchmarker.bmcontroller.user.controller.dto.GroupInfo;
import org.benchmarker.bmcontroller.user.controller.dto.GroupUpdateDto;
import org.benchmarker.bmcontroller.user.controller.dto.UserGroupRoleInfo;
import org.benchmarker.bmcontroller.user.helper.UserHelper;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.model.enums.GroupRole;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers(parallel = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GroupServiceTest {
    @SpyBean
    private GroupService groupService;
    @SpyBean
    private UserGroupRepository userGroupRepository;
    @SpyBean
    private UserRepository userRepository;
    @SpyBean
    private UserGroupJoinRepository userGroupJoinRepository;
    @SpyBean
    private TestResultRepository testResultRepository;
    @SpyBean
    private TestMttfbRepository mttfbRepository;
    @SpyBean
    private TestTpsRepository tpsRepository;
    @SpyBean
    private TestTemplateRepository testTemplateRepository;

    @BeforeEach
    void removeAll() {
        mttfbRepository.deleteAll();
        tpsRepository.deleteAll();
        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();
        userGroupJoinRepository.deleteAll();
        userRepository.deleteAll();
        userGroupRepository.deleteAll();
    }

    @Nested
    @DisplayName("그룹 생성")
    class test01 {

        @Test
        @DisplayName("그룹 정상 생성 시 그룹 Info 를 반환한다")
        public void test1() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            String groupId = "groupId";
            String groupName = "groupName";

            // when
            GroupInfo groupInfo = groupService.createGroup(GroupAddDto.builder()
                .id(groupId)
                .name(groupName)
                .build(), defaultUser.getId());

            // then
            assertThat(groupInfo.getId()).isEqualTo("groupId");
            assertThat(groupInfo.getName()).isEqualTo("groupName");


            List<UserGroupJoin> findUserJoin = userGroupJoinRepository.findByUserId(
                defaultUser.getId());
            assertThat(findUserJoin).hasSize(1);
            assertThat(findUserJoin.get(0).getUser().getId()).isEqualTo(defaultUser.getId());
            assertThat(findUserJoin.get(0).getUserGroup().getId()).isEqualTo(groupId);
            assertThat(findUserJoin.get(0).getRole()).isEqualTo(GroupRole.LEADER);
        }

        @Test
        @DisplayName("그룹 생성 시 이미 존재하는 그룹이면 GloablException 에러를 반환한다")
        public void test2() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            String groupId = "groupId";
            String groupName = "groupName";
            UserGroup userGroup = UserGroup.builder()
                .id(groupId)
                .name(groupName)
                .build();
            userGroupRepository.save(userGroup);
            GroupAddDto groupAddDto = GroupAddDto.builder()
                .id(groupId)
                .name(groupName)
                .build();

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.createGroup(groupAddDto, defaultUser.getId());
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.GROUP_ALREADY_EXIST);
        }
    }

    @Nested
    @DisplayName("그룹에 사용자 추가")
    class test02 {
        @Test
        @DisplayName("그룹에 사용자 추가 시, 해당 사용자가 존재하지 않으면 GlobalException 을 반환한다")
        public void test21() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);

            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(), GroupRole.LEADER);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.addUserToGroup(userGroup.getId(), defaultUser.getId(), otherUser.getId(),
                    GroupRole.MEMBER);
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND);

        }

        @Test
        @DisplayName("어드민이 그룹에 사용자 추가 시, 해당 사용자가 존재하지 않으면 GlobalException 을 반환한다")
        public void test22() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                    GroupRole.MEMBER);
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND);

        }

        @Test
        @DisplayName("어드민이 다른 유저를 그룹에 추가하고 정보를 받아온다")
        public void test3() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);

            // when
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.MEMBER);
            GroupInfo groupInfo = groupService.getGroupInfoAdmin(userGroup.getId());

            // then
            assertThat(groupInfo.getId()).isEqualTo(userGroup.getId());
            assertThat(groupInfo.getName()).isEqualTo(userGroup.getName());

            assertThat(groupInfo.getUsers()).hasSize(1);
        }

        @Test
        @DisplayName("일반 유저는 다른 유저를 그룹에 추가하고 정보를 받아온다")
        public void test4() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);

            // when
            groupService.addUserToGroup(userGroup.getId(), defaultUser.getId(), otherUser.getId(),
                GroupRole.MEMBER);
            GroupInfo groupInfo = groupService.getGroupInfo(userGroup.getId(), defaultUser.getId());

            // then
            assertThat(groupInfo.getId()).isEqualTo(userGroup.getId());
            assertThat(groupInfo.getName()).isEqualTo(userGroup.getName());
            assertThat(groupInfo.getUsers()).hasSize(2);
        }

        @Test
        @DisplayName("일반 유저는 그룹에 속해있지 않다면, 다른 유저를 그룹에 추가할 때 GlobalException 에러를 반환한다")
        public void test5() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.addUserToGroup(userGroup.getId(), defaultUser.getId(),
                    otherUser.getId(),
                    GroupRole.MEMBER);
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_IN_GROUP);
        }

        @Test
        @DisplayName("일반 유저는 그룹의 리더가 아니라면, 다른 유저를 그룹에 추가할 때 GlobalException 에러를 반환한다")
        public void test6() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);

            // when
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.MEMBER); // 맴버로 넣음

            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.addUserToGroup(userGroup.getId(), defaultUser.getId(),
                    otherUser.getId(),
                    GroupRole.MEMBER);
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("그룹 정보 조회")
    class test03 {

        @Test
        @WithMockUser(username = "adminId", roles = {"ADMIN"})
        @DisplayName("어드민 유저는 그룹 정보를 Pageable 로 조회할 수 있다")
        public void test335() {
            // Given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);
            groupService.addUserToGroupAdmin(userGroup.getId(), otherUser.getId(),
                GroupRole.MEMBER);

            // when
            Pageable pageable = PageRequest.of(0, 5);

            Page<GroupInfo> allGroupInfo = groupService.getAllGroupInfoAdmin(pageable);
            Optional<GroupInfo> foundGroup = allGroupInfo.stream()
                .filter(groupInfo -> groupInfo.getId().equals(userGroup.getId()))
                .findFirst();

            // When, Then
            assertThat(foundGroup).isPresent();
            assertThat(foundGroup.get().getId()).isEqualTo(userGroup.getId());
            assertThat(foundGroup.get().getName()).isEqualTo(userGroup.getName());
            assertThat(foundGroup.get().getUsers()).hasSize(2);
        }

        @Test
        @WithMockUser(username = "adminId", roles = {"ADMIN"})
        @DisplayName("어드민 유저는 모든 그룹 정보를 조회할 수 있다")
        public void test336() {
            // Given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);
            groupService.addUserToGroupAdmin(userGroup.getId(), otherUser.getId(),
                GroupRole.MEMBER);

            UserGroupRoleInfo userGroupRoleInfoUser = UserGroupRoleInfo.builder()
                .id(defaultUser.getId())
                .role(GroupRole.LEADER)
                .build();
            UserGroupRoleInfo userGroupRoleInfoOther = UserGroupRoleInfo.builder()
                .id(otherUser.getId())
                .role(GroupRole.MEMBER)
                .build();

            // when
            List<GroupInfo> allGroupInfo = groupService.getAllGroupInfoAdmin();
            Optional<GroupInfo> foundGroup = allGroupInfo.stream()
                .filter(groupInfo -> groupInfo.getId().equals(userGroup.getId()))
                .findFirst();

            // When, Then
            assertThat(foundGroup).isPresent();
            assertThat(foundGroup.get().getId()).isEqualTo(userGroup.getId());
            assertThat(foundGroup.get().getName()).isEqualTo(userGroup.getName());
            assertThat(foundGroup.get().getUsers()).hasSize(2);

        }

        @Test
        @DisplayName("일반 유저는 자신의 그룹 정보를 조회할 수 있다")
        public void test333() {
            // Given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);
            groupService.addUserToGroupAdmin(userGroup.getId(), otherUser.getId(),
                GroupRole.MEMBER);

            // when
            List<GroupInfo> allGroupInfo = groupService.getAllGroupInfo(defaultUser.getId());

            // When, Then
            assertThat(allGroupInfo).hasSize(1);
            assertThat(allGroupInfo.get(0).getId()).isEqualTo(userGroup.getId());
            assertThat(allGroupInfo.get(0).getName()).isEqualTo(userGroup.getName());
            assertThat(allGroupInfo.get(0).getUsers()).hasSize(2);
        }

        @Test
        @DisplayName("일반 유저는 자신의 그룹 정보를 pageable 로 조회할 수 있다")
        public void test334() {
            // Given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);
            groupService.addUserToGroupAdmin(userGroup.getId(), otherUser.getId(),
                GroupRole.MEMBER);

            // when
            Pageable pageable = PageRequest.of(0, 1);
            Page<GroupInfo> allGroupInfo = groupService.getAllGroupInfo(defaultUser.getId(),
                pageable);

            // When, Then
            assertThat(allGroupInfo).hasSize(1);
            assertThat(allGroupInfo.getContent().get(0).getId()).isEqualTo(userGroup.getId());
            assertThat(allGroupInfo.getContent().get(0).getName()).isEqualTo(userGroup.getName());
            assertThat(allGroupInfo.getContent().get(0).getUsers()).hasSize(2);
        }

        @Test
        @DisplayName("어드민 유저는 그룹 조회 시, 그룹이 존재하지 않으면 GlobalException 을 반환한다")
        public void test18() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
//        userGroupRepository.save(userGroup);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.getGroupInfoAdmin(userGroup.getId());
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.GROUP_NOT_FOUND);
        }

        @Test
        @DisplayName("일반 유저는 그룹 조회 시, 유저가 존재하지 않으면 GlobalException 을 반환한다")
        public void test19() {
            // given
            User defaultUser = UserHelper.createDefaultUser();

            UserGroup userGroup = UserHelper.createDefaultUserGroup();

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.getGroupInfo(userGroup.getId(), defaultUser.getId());
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("일반 유저는 그룹 조회 시, 그룹이 존재하지 않으면 GlobalException 을 반환한다")
        public void test20() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
//        userGroupRepository.save(userGroup);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.getGroupInfo(userGroup.getId(), defaultUser.getId());
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.GROUP_NOT_FOUND);
        }

        @Test
        @DisplayName("일반 유저는 자신이 그룹에 없을 때, 다른 그룹 정보를 가져올 때 GlobalException 을 반환한다")
        public void test7() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), otherUser.getId(),
                GroupRole.MEMBER);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                GroupInfo groupInfo = groupService.getGroupInfo(userGroup.getId(),
                    defaultUser.getId());
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_IN_GROUP);
        }
    }

    @Nested
    @DisplayName("그룹 정보 업데이트")
    class test04 {
        @Test
        @DisplayName("그룹 정보를 업데이트 하면 업데이트된 정보를 반환한다")
        public void test8() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            GroupUpdateDto updateDto = GroupUpdateDto.builder()
                .name("updatedName")
                .build();
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.MEMBER);

            // when
            groupService.updateGroupUser(updateDto, userGroup.getId(), defaultUser.getId());
            GroupInfo groupInfo = groupService.getGroupInfo(userGroup.getId(), defaultUser.getId());

            // then
            assertThat(groupInfo.getId()).isEqualTo(userGroup.getId());
            assertThat(groupInfo.getName()).isEqualTo("updatedName");
        }

        @Test
        @DisplayName("그룹에 속해있지 않은 유저는 그룹 정보 업데이트 시 GlobalException 을 반환한다")
        public void test9() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            GroupUpdateDto updateDto = GroupUpdateDto.builder()
                .name("updatedName")
                .build();

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.updateGroupUser(updateDto, userGroup.getId(), defaultUser.getId());
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_IN_GROUP);
        }

        @Test
        @DisplayName("어드민 유저는 그룹 정보를 업데이트하면 업데이트된 정보를 반환한다")
        public void test10() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            GroupUpdateDto updateDto = GroupUpdateDto.builder()
                .name("updatedName")
                .build();

            // when
            GroupInfo groupInfo = groupService.updateGroupAdmin(updateDto, userGroup.getId());

            // then
            assertThat(groupInfo.getId()).isEqualTo(userGroup.getId());
            assertThat(groupInfo.getName()).isEqualTo("updatedName");
        }

        @Test
        @DisplayName("어드민 유저는 그룹 정보를 업데이트 시, 그룹이 없다면 GlobalException 을 반환한다")
        public void test11() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            GroupUpdateDto updateDto = GroupUpdateDto.builder()
                .name("updatedName")
                .build();

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.updateGroupAdmin(updateDto, userGroup.getId());
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.GROUP_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("그룹 삭제")
    class test05 {

        @Test
        @DisplayName("그룹 삭제 성공")
        public void testDeleteGroupSuccess() {
            // Given
            String groupId = "groupId";
            String myId = "myId";
            User defaultUser = UserHelper.createDefaultUser();
            UserGroup foundGroup = new UserGroup(groupId, "Group 1");
            UserGroupJoin join = UserGroupJoin.builder()
                .user(defaultUser)
                .userGroup(foundGroup)
                .role(GroupRole.LEADER)
                .build();


            when(userGroupRepository.findById(groupId)).thenReturn(java.util.Optional.of(foundGroup));
            when(userGroupJoinRepository.findByUserIdAndUserGroupId(myId, groupId)).thenReturn(java.util.Optional.of(join));

            // When
            groupService.deleteGroup(groupId, myId);

            // Then
            // Verify that group and join are deleted
            verify(userGroupJoinRepository, times(1)).deleteAllByUserGroupId(groupId);
            verify(userGroupRepository, times(1)).delete(foundGroup);
        }

        @Test
        @DisplayName("유저는 그룹 삭제 시, 그룹의 LEADER 가 아니라면 GlobalException 을 반환한다 - 2")
        public void testDeleteGroupUserNotLeader() {
            // Given
            User defaultUser = UserHelper.createDefaultUser();
            User otherUser = UserHelper.createDefaultUser("otherId");
            userRepository.save(defaultUser);
            userRepository.save(otherUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);
            groupService.addUserToGroupAdmin(userGroup.getId(), otherUser.getId(),
                GroupRole.MEMBER);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.deleteGroup(userGroup.getId(), otherUser.getId());
            }).getErrorCode();

            // When, Then
            assertThat(errorCode).isEqualTo(ErrorCode.FORBIDDEN);
        }

        @Test
        @DisplayName("어드민 유저로 그룹 삭제 성공")
        public void testDeleteGroupAdminSuccess() {
            // Given
            String groupId = "groupId";
            UserGroup foundGroup = new UserGroup(groupId, "Group 1");

            when(userGroupRepository.findById(groupId)).thenReturn(java.util.Optional.of(foundGroup));

            // When
            groupService.deleteGroupAdmin(groupId);

            // Then
            // Verify that group is deleted
            verify(userGroupRepository, times(1)).delete(foundGroup);
        }

        @Test
        @DisplayName("어드민 유저로 그룹 삭제 시, 그룹 미발견하면 GlobalException 반환")
        public void testDeleteGroupAdminGroupNotFound() {
            // Given
            String groupId = "groupId";

            when(userGroupRepository.findById(groupId)).thenReturn(java.util.Optional.empty());

            // When, Then
            assertThrows(GlobalException.class, () -> groupService.deleteGroupAdmin(groupId));
        }
        @Test
        @DisplayName("유저는 그룹 삭제 시, 그룹이 없으면 GlobalException 을 반환한다")
        public void test12() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.deleteUserFromGroup(userGroup.getId(), defaultUser.getId(),
                    defaultUser.getId(), false);
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.FORBIDDEN);
        }

        @Test
        @DisplayName("유저는 그룹 삭제 시, 그룹에 속해있지 않다면 GlobalException 을 반환한다")
        public void test13() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.deleteUserFromGroup(userGroup.getId(), defaultUser.getId(),
                    defaultUser.getId(), false);
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.FORBIDDEN);
        }

        @Test
        @DisplayName("유저는 그룹 삭제 시, 그룹의 LEADER 가 아니라면 GlobalException 을 반환한다")
        public void test14() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.MEMBER);

            // when
            ErrorCode errorCode = assertThrows((GlobalException.class), () -> {
                groupService.deleteUserFromGroup(userGroup.getId(), defaultUser.getId(),
                    defaultUser.getId(), false);
            }).getErrorCode();

            // then
            assertThat(errorCode).isEqualTo(ErrorCode.FORBIDDEN);
        }

        @Test
        @DisplayName("유저는 그룹 삭제 시, 정상 삭제하면 업데이트 된 그룹 정보를 반환한다")
        public void test15() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);

            // when
            GroupInfo groupInfo = groupService.deleteUserFromGroup(userGroup.getId(),
                defaultUser.getId(),
                defaultUser.getId(), false);

            // then
            assertThat(groupInfo.getId()).isEqualTo(userGroup.getId());
            assertThat(groupInfo.getUsers()).isEmpty();
        }

        @Test
        @DisplayName("유저는 그룹 삭제 시, 정상 삭제하면 업데이트 된 그룹 정보를 반환한다")
        public void test16() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);

            // when
            GroupInfo groupInfo = groupService.deleteUserFromGroup(userGroup.getId(),
                defaultUser.getId(),
                defaultUser.getId(), false);

            // then
            assertThat(groupInfo.getId()).isEqualTo(userGroup.getId());
            assertThat(groupInfo.getUsers()).isEmpty();
        }

        @Test
        @DisplayName("어드민 유저는 그룹 삭제 시, 정상 삭제하면 업데이트 된 그룹 정보를 반환한다")
        public void test17() {
            // given
            User defaultUser = UserHelper.createDefaultUser();
            userRepository.save(defaultUser);
            UserGroup userGroup = UserHelper.createDefaultUserGroup();
            userGroupRepository.save(userGroup);
            groupService.addUserToGroupAdmin(userGroup.getId(), defaultUser.getId(),
                GroupRole.LEADER);

            // when
            GroupInfo groupInfo = groupService.deleteUserFromGroup(userGroup.getId(),
                defaultUser.getId(),
                defaultUser.getId(), true);

            // then
            assertThat(groupInfo.getId()).isEqualTo(userGroup.getId());
            assertThat(groupInfo.getUsers()).isEmpty();
        }
    }



}