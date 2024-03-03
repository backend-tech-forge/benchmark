package org.benchmarker.user.service;

import org.benchmarker.user.model.User;

import java.util.*;


public interface IUserService {

    Optional<User> createUser(User user);

    User getUser(String id);

    List<User> getUsers();

    Optional<User> updateUser(User user) throws Exception;

    void deleteUser(String id);
}
