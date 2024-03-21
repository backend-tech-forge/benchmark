package org.benchmarker.bmcontroller.user.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.bmcontroller.user.controller.dto.UserInfo;
import org.benchmarker.bmcontroller.user.controller.dto.UserRegisterDto;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.benchmarker.bmcontroller.user.service.UserService;
import org.benchmarker.bmcontroller.user.controller.dto.UserUpdateDto;
import org.benchmarker.bmcontroller.user.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/user/update")
    @PreAuthorize("hasAnyRole('USER')")
    public String updateUser(Model model) {
        User currentUser = userContext.getCurrentUser();
        UserInfo userInfo = userService.getUser(currentUser.getId()).get();
        model.addAttribute("userUpdateDto", UserUpdateDto.builder()
            .id(userInfo.getId())
            .email(userInfo.getEmail())
            .slackNotification(userInfo.getSlackNotification())
            .slackWebhookUrl(userInfo.getSlackWebhookUrl())
            .emailNotification(userInfo.getEmailNotification())
            .userGroup(userInfo.getUserGroup())
            .build());
        return "user/updateInfo";
    }

    @PostMapping("/user/update")
    @PreAuthorize("hasAnyRole('USER')")
    public String updateUser(@Validated @ModelAttribute("userUpdateDto") UserUpdateDto userRegisterDto, BindingResult bindingResult,
        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "user/updateInfo";
        }

        userService.updateUser(userRegisterDto);
        return "redirect:/user";
    }

}
