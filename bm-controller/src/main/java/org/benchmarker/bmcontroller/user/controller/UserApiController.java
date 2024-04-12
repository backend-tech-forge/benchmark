package org.benchmarker.bmcontroller.user.controller;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.user.controller.dto.UserInfo;
import org.benchmarker.bmcontroller.user.controller.dto.UserRegisterDto;
import org.benchmarker.bmcontroller.user.controller.dto.UserUpdateDto;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.service.IUserService;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<UserInfo> createUser(@Validated @RequestBody UserRegisterDto userRegisterDto) {
        Optional<UserInfo> userInfo = userService.createUser(userRegisterDto);
        return ResponseEntity.ok(userInfo.get());
    }

    @GetMapping({"/user", "/users/{user_id}"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<UserInfo> getUser(
        @PathVariable(required = false,name = "user_id") String user_id) {
        User currentUser = userContext.getCurrentUser();
        if (user_id == null) {
            UserInfo userInfo = userService.getUser(currentUser.getId()).get();
            return ResponseEntity.ok(userInfo);
        } else {
            if (currentUser.getRole().isAdmin()) {
                Optional<UserInfo> userInfo = userService.getUser(user_id);
                return ResponseEntity.ok(userInfo.get());
            } else {
                UserInfo user = userService.getUserIfSameGroup(currentUser.getId(), user_id);
                return ResponseEntity.ok(user);
            }
        }
    }

    @PatchMapping("/user")
    public ResponseEntity<UserInfo> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        UserInfo userInfo = userService.updateUser(userUpdateDto)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/users/{user_id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable("user_id") String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> deleteCurrentUser() {
        User currentUser = userContext.getCurrentUser();
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserInfo>> getUsers() {
        List<User> users = userService.getUsers();
        List<UserInfo> userInfo = users.stream().map(UserInfo::from).collect(Collectors.toList());
        return ResponseEntity.ok(userInfo);
    }

}
