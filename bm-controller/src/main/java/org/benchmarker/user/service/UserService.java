package org.benchmarker.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.benchmarker.user.constant.UserConsts.USER_GROUP_DEFAULT_ID;

@Service("userService")
@RequiredArgsConstructor
public class UserService extends AbstractUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupRepository userGroupRepository;

    @Override
    @Transactional
    public Optional<User> createUser(User user) {
        userRepository.findById(user.getId()).ifPresent((u) -> {
            throw new GlobalException(ErrorCode.USER_ALREADY_EXIST);
        });
        if (user.getUserGroup() == null) {
            UserGroup defaultGroup = userGroupRepository.findById(USER_GROUP_DEFAULT_ID)
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));
            user.setUserGroup(defaultGroup);
        }else{
            userGroupRepository.findById(user.getUserGroup().getId()).ifPresentOrElse(
                (group) -> {
                    throw new GlobalException(ErrorCode.GROUP_ALREADY_EXIST);
                },
                () -> {
                    userGroupRepository.save(user.getUserGroup());
                }
            );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // bcrypt encoding
        return Optional.of(userRepository.save(user));
    }

    @Override
    @Transactional
    public User getUser(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public User getUserIfSameGroup(String currentUserId, String id) {
        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        User user = userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        if (!currentUser.getUserGroup().equals(user.getUserGroup())) {
            throw new GlobalException(ErrorCode.USER_NOT_SAME_GROUP);
        }

        return user;
    }

    @Transactional
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<User> updateUser(User user) throws Exception {
        userRepository.findById(user.getId())
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        User save = userRepository.save(user);
        return Optional.of(save);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        userRepository.deleteById(id);
    }

}
