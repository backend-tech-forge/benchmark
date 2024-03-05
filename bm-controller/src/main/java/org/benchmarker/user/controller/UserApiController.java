package org.benchmarker.user.controller;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.model.User;
import org.benchmarker.user.service.IUserService;
import org.benchmarker.user.service.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {

    private final IUserService userService;
    private final UserContext userContext;

    @PostMapping("/user")
    public ResponseEntity<UserInfo> createUser(@RequestBody UserRegisterDto userRegisterDto) {
        User user = userRegisterDto.toEntity();
        Optional<User> savedUser = userService.createUser(user);
        return ResponseEntity.ok(UserInfo.from(savedUser.get()));
    }

    @GetMapping({"/user", "/user/{user_id}"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<UserInfo> getUser(
        @PathVariable(required = false) String user_id) {
        User currentUser = userContext.getCurrentUser();
        if (user_id == null) {
            return ResponseEntity.ok(UserInfo.from(currentUser));
        } else {
            if (currentUser.getRole().isAdmin()) {
                User user = userService.getUser(user_id);
                return ResponseEntity.ok(UserInfo.from(user));
            } else {
                User user = userService.getUserIfSameGroup(currentUser.getId(), user_id);
                return ResponseEntity.ok(UserInfo.from(user));
            }
        }
    }


    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserInfo>> getUsers() {
        List<User> users = userService.getUsers();
        List<UserInfo> userInfo = users.stream().map(UserInfo::from).collect(Collectors.toList());
        return ResponseEntity.ok(userInfo);
    }
}
