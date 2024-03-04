package org.benchmarker.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.login.controller.dto.LoginRequestInfo;
import org.benchmarker.user.controller.dto.UserRegisterDto;
import org.benchmarker.user.model.User;
import org.benchmarker.user.service.UserContext;
import org.benchmarker.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        return "user/userRegister";
    }

    @PostMapping("/user")
    public String saveUser(@Validated @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto, BindingResult bindingResult,
        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "user/userRegister";
        }

        User user = userRegisterDto.toEntity();
        userService.createUser(user);
        return "redirect:/home";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model) {
        List<User> users = userService.getUsers();
        model.addAttribute("users", users);
        return "userList";
    }


}
