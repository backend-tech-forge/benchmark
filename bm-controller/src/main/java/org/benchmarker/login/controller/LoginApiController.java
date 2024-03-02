package org.benchmarker.login.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.benchmarker.login.service.LoginService;
import org.benchmarker.login.controller.dto.LoginRequestInfo;
import org.benchmarker.user.service.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.benchmarker.security.constant.MessageConsts.LOGOUT_SUCCESS;
import static org.benchmarker.security.constant.TokenConsts.ACCESS_TOKEN_COOKIE_NAME;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginApiController {
    private final LoginService loginService;
    private final UserContext userContext;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestInfo req, HttpServletResponse resp) {
        String accessToken = loginService.login(req);
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        resp.addCookie(cookie);
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> logout(HttpServletResponse resp) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        return ResponseEntity.ok(LOGOUT_SUCCESS);
    }
}
