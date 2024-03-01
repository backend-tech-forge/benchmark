package org.benchmarker.user.service;

import org.benchmarker.user.model.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> createUser(User user);
    Optional<User> getUser(String id);
    Optional<User> updateUser(User user) throws Exception;
    void deleteUser(String id);
}
