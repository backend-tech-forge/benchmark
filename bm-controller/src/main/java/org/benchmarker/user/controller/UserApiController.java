package org.benchmarker.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.dto.UserDto;
import org.benchmarker.user.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {
    private final IUserService userService;

    @PostMapping("/create")
    public ResponseEntity createUser(@RequestBody UserDto userDto) {
        User user = userDto.toEntity();
        return ResponseEntity.ok(userService.createUser(user));
    }
}
