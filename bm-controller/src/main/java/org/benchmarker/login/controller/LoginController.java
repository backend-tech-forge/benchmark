package org.benchmarker.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.common.util.CookieUtil;
import org.benchmarker.login.service.LoginService;
import org.benchmarker.login.controller.dto.LoginRequestInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static org.benchmarker.security.constant.TokenConsts.ACCESS_TOKEN_COOKIE_NAME;

@Slf4j
@Controller
@GlobalControllerModel
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String showLoginForm(@ModelAttribute("loginRequestDto") LoginRequestInfo req, Model model) {
        return "login"; // login.html 템플릿을 렌더링
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginRequestDto") LoginRequestInfo req, BindingResult bindingResult,
                        HttpServletResponse resp) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        String token = loginService.login(req);

        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, token);
        resp.addCookie(cookie);

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse resp) {
        CookieUtil.removeCookie(resp, ACCESS_TOKEN_COOKIE_NAME);
        log.info("LogOUT!!!");
        return "redirect:/home";
    }
}