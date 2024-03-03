package org.benchmarker.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.model.Role;
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
    public ResponseEntity createUser(@RequestBody UserRegisterDto userRegisterDto) {
        User user = userRegisterDto.toEntity();
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserInfo> getUser(@RequestParam String id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(UserInfo.from(user));
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserInfo> getUser() {
        User currentUser = userContext.getCurrentUser();
        return ResponseEntity.ok(UserInfo.from(currentUser));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserInfo>> getUsers() {
        List<User> users = userService.getUsers();
        List<UserInfo> userInfo = users.stream().map(UserInfo::from).collect(Collectors.toList());
        return ResponseEntity.ok(userInfo);
    }
}
