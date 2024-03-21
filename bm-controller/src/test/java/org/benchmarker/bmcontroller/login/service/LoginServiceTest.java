package org.benchmarker.bmcontroller.login.service;

import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.login.controller.dto.LoginRequestInfo;
import org.benchmarker.bmcontroller.security.JwtTokenProvider;
import org.benchmarker.bmcontroller.user.controller.constant.TestUserConsts;
import org.benchmarker.bmcontroller.user.model.enums.Role;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("유효한 사용자 정보로 로그인 성공")
    public void testLogin_ValidCredentials() {
        // given
        String userId = TestUserConsts.id;
        String password = TestUserConsts.password;
        String encodedPassword = "encodedPassword";
        Role userRole = Role.ROLE_USER;
        LoginRequestInfo loginRequestInfo = new LoginRequestInfo(userId, password);
        User user = new User();
        user.setId(userId);
        user.setPassword(encodedPassword);
        user.setRole(userRole);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(userId, userRole)).thenReturn("accessToken");

        // when
        String token = loginService.login(loginRequestInfo);

        // then
        assertNotNull(token);
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 경우 GlobalException 발생")
    public void testLogin_InvalidUserId() {
        // given
        String userId = "nonexistentUser";
        String password = "testPassword";
        LoginRequestInfo loginRequestInfo = new LoginRequestInfo();
        loginRequestInfo.setId(userId);
        loginRequestInfo.setPassword(password);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(GlobalException.class, () -> loginService.login(loginRequestInfo));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않는 경우 GlobalException 발생")
    public void testLogin_IncorrectPassword() {
        // given
        String userId = TestUserConsts.id;
        String password = "incorrectPassword";
        String encodedPassword = "encodedPassword";
        LoginRequestInfo loginRequestInfo = LoginRequestInfo.builder()
            .id(userId)
            .password(password)
            .build();

        User user = new User();
        user.setId(userId);
        user.setPassword(encodedPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // when, then
        assertThrows(GlobalException.class, () -> loginService.login(loginRequestInfo));
    }
}