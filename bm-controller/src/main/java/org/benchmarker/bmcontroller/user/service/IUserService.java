package org.benchmarker.bmcontroller.user.service;

import org.benchmarker.bmcontroller.user.controller.dto.UserInfo;
import org.benchmarker.bmcontroller.user.controller.dto.UserRegisterDto;
import org.benchmarker.bmcontroller.user.controller.dto.UserUpdateDto;
import org.benchmarker.bmcontroller.user.model.User;

import java.util.*;


public interface IUserService {

    Optional<UserInfo> createUser(UserRegisterDto userRegisterDto);

    Optional<UserInfo> getUser(String id);

    UserInfo getUserIfSameGroup(String currentUserId, String id);

    List<User> getUsers();

    Optional<UserInfo> updateUser(UserUpdateDto user);

    void deleteUser(String id);
}
