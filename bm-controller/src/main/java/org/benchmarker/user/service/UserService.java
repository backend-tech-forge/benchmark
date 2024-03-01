package org.benchmarker.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userService")
@RequiredArgsConstructor
public class UserService extends AbstractUserService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    @Override
    @Transactional
    public Optional<User> createUser(User user) {
        userRepository.findById(user.getId()).ifPresent((u) -> {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        });
        UserGroup defaultGroup = userGroupRepository.findById("default").orElseThrow(()->new GlobalException(ErrorCode.GROUP_NOT_FOUND));
        user.setUserGroup(defaultGroup);
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> getUser(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<User> updateUser(User user) throws Exception {
        userRepository.findById(user.getId()).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        User save = userRepository.save(user);
        return Optional.of(save);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        userRepository.deleteById(id);
    }

}
