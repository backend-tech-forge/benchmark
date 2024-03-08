package org.benchmarker.user.service;

import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.controller.dto.UserUpdateDto;
import org.benchmarker.user.model.User;

import java.util.*;


public interface IUserService {

    Optional<UserInfo> createUser(UserRegisterDto userRegisterDto);

    Optional<UserInfo> getUser(String id);

    UserInfo getUserIfSameGroup(String currentUserId, String id);

    List<User> getUsers();

    Optional<UserInfo> updateUser(UserUpdateDto user) throws Exception;

    void deleteUser(String id);
}
