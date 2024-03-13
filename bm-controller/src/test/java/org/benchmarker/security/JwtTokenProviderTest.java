package org.benchmarker.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.benchmarker.security.constant.TokenConsts;
import org.benchmarker.user.controller.constant.TestUserConsts;
import org.benchmarker.user.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.util.random.RandomUtil;

@SpringBootTest
public class JwtTokenProviderTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.setSecret(RandomUtil.generateRandomString(128));
        jwtTokenProvider.setExpirationTime("1000000");
        jwtTokenProvider.setRefreshExpirationTime("1000000");

    }

    @Test
    @DisplayName("Access Token을 생성한다")
    public void testCreateAccessToken_auth() {
        // given
        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken("username", "password", "ROLE_USER");
        authenticationToken.setAuthenticated(true);

        // when
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        // then
        assertThat(accessToken).isNotEmpty();
    }

    @Test
    @DisplayName("Refresh Token을 생성한다")
    public void testCreateRefreshToken() {
        // given
        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken("username", "password", "ROLE_USER");
        authenticationToken.setAuthenticated(true);

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // then
        assertThat(refreshToken).isNotEmpty();
    }

    @Test
    @DisplayName("토큰에서 Authentication 객체를 가져온다")
    public void testGetAuthentication() {
        // given
        String username = TestUserConsts.id;
        String password = TestUserConsts.password;
        String userRole = "ROLE_USER";

        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(username, password, userRole);
        authenticationToken.setAuthenticated(true);
        String token = jwtTokenProvider.createAccessToken(authenticationToken);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(username);
        assertThat(authentication.getAuthorities()).isEqualTo(authenticationToken.getAuthorities());
    }

    @Test
    @DisplayName("expired 된 토큰이 유효한지 확인한다")
    public void testValidateToken_ExpiredToken() {
        // given
        jwtTokenProvider.setExpirationTime("-10000");
        String username = TestUserConsts.id;
        String password = TestUserConsts.password;
        String userRole = "ROLE_USER";

        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(username, password, userRole);
        authenticationToken.setAuthenticated(true);

        String expiredToken = jwtTokenProvider.createAccessToken(authenticationToken);

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("valid 한 토큰이 유효한지 확인한다")
    public void testValidateToken() {
        // given
        String username = TestUserConsts.id;
        String password = TestUserConsts.password;
        String userRole = "ROLE_USER";

        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(username, password, userRole);
        authenticationToken.setAuthenticated(true);

        String expiredToken = jwtTokenProvider.createAccessToken(authenticationToken);

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("쿠키에서 토큰을 가져와 userId를 반환한다")
    public void testValidateTokenAndGetUserId() {
        // given
        String username = TestUserConsts.id;
        String password = TestUserConsts.password;
        String userRole = "ROLE_USER";

        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(username, password, userRole);
        authenticationToken.setAuthenticated(true);

        String cookieName = TokenConsts.ACCESS_TOKEN_COOKIE_NAME;
        String token = jwtTokenProvider.createAccessToken(authenticationToken);
        Cookie cookie = new Cookie(cookieName, token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // when
        String userId = jwtTokenProvider.validateTokenAndGetUserId(request, cookieName);

        // then
        assertThat(userId).isEqualTo(username);
    }

    @Test
    @DisplayName("쿠키에서 토큰을 가져와 userId를 반환할 때, 쿠키가 없는 경우 null을 반환한다")
    public void testValidateTokenAndGetUserId_WithNoCookie() {
        // given
        String username = TestUserConsts.id;
        String password = TestUserConsts.password;
        String userRole = "ROLE_USER";

        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(username, password, userRole);
        authenticationToken.setAuthenticated(true);

        String cookieName = TokenConsts.ACCESS_TOKEN_COOKIE_NAME;
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // when
        String userId = jwtTokenProvider.validateTokenAndGetUserId(request, cookieName);

        // then
        assertThat(userId).isEqualTo(null);
    }

    @Test
    @DisplayName("username, role을 받아 AccessToken을 생성한다")
    public void testCreateAccessToken_userame_role() {
        // given
        String username = TestUserConsts.id;
        String password = TestUserConsts.password;
        String userRole = Role.ROLE_USER.name();

        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(username, password, userRole);
        authenticationToken.setAuthenticated(true);

        // when
        String userId = jwtTokenProvider.createAccessToken(username, Role.ROLE_USER);

        // then
        assertThat(userId).isNotEmpty();
    }

}