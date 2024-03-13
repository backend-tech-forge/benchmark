package org.benchmarker.user.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.user.controller.dto.UserInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.service.UserContext;
import org.benchmarker.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@GlobalControllerModel
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @GetMapping("/user")
    public String user(@ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto) {
        userRegisterDto.setUserGroup(List.of());
        return "user/register";
    }

    @GetMapping({"/users/{user_id}"})
    @PreAuthorize("hasAnyRole('USER')")
    public String getUser(
        @PathVariable(required = false) String user_id, Model model) {
        User currentUser = userContext.getCurrentUser();
        UserInfo userInfo = null;
        if (user_id == null) {
            userInfo = userService.getUser(currentUser.getId()).get();
        } else {
            if (currentUser.getRole().isAdmin()) {
                Optional<UserInfo> userInfoOp = userService.getUser(user_id);
                userInfo = userInfoOp.get();
            } else {
                userInfo = userService.getUserIfSameGroup(currentUser.getId(), user_id);
            }
        }

        model.addAttribute("userInfo", userInfo);
        return "user/info";
    }

    @PostMapping("/user")
    public String saveUser(@Validated @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto, BindingResult bindingResult,
        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "user/register";
        }

        userService.createUser(userRegisterDto);
        return "redirect:/home";
    }

}
