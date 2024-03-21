package org.benchmarker.bmcontroller.user.util;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.user.controller.dto.UserGroupRoleInfo;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.user.controller.dto.GroupInfo;
import org.benchmarker.bmcontroller.user.controller.dto.UserUpdateDto;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserServiceUtils {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;
    private final TestTemplateRepository testTemplateRepository;


    /**
     * Get a {@link GroupInfo} by userId and groupId
     *
     * <p>if user does not participate in the group, {@link ErrorCode#USER_NOT_IN_GROUP}
     * occurred</p>
     *
     * @param groupId
     * @param userId
     * @return {@link GroupInfo}
     * @throws GlobalException <p>{@link ErrorCode#USER_NOT_IN_GROUP}</p>
     *                         <p>{@link ErrorCode#GROUP_NOT_FOUND}</p>
     *                         <p>{@link ErrorCode#USER_NOT_IN_GROUP}</p>
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public GroupInfo getGroupInfo(String groupId, String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        userGroupRepository.findById(groupId)
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));

        // if user is not in the group, throw exception
        if (user.getUserGroupJoin().stream()
            .noneMatch((j) -> j.getUserGroup().getId().equals(groupId))) {
            throw new GlobalException(ErrorCode.USER_NOT_IN_GROUP);
        }

        List<TestTemplateResponseDto> list = testTemplateRepository.findAllByUserGroupId(groupId)
            .stream().map(TestTemplate::convertToResponseDto).toList();

        List<UserGroupRoleInfo> collect = userGroupJoinRepository.findByUserGroupId(groupId)
            .stream()
            .map((userGroupJoin -> {
                User joinUser = userGroupJoin.getUser();
                return UserGroupRoleInfo.builder()
                    .id(joinUser.getId())
                    .role(userGroupJoin.getRole())
                    .build();
            })).collect(Collectors.toList());

        return GroupInfo.builder()
            .id(groupId)
            .users(collect)
            .name(userGroupRepository.findById(groupId).get().getName())
            .templates(list)
            .build();
    }

    /**
     * Update user with {@link UserUpdateDto}
     *
     * @param user {@link User}
     * @param req {@link UserUpdateDto}
     * @return {@link User}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public User updateUser(User user, UserUpdateDto req){
        user.setEmail(req.getEmail());
        user.setEmailNotification(req.getEmailNotification());
        user.setSlackWebhookUrl(req.getSlackWebhookUrl());
        user.setSlackNotification(req.getSlackNotification());
        return userRepository.save(user);
    }

}
