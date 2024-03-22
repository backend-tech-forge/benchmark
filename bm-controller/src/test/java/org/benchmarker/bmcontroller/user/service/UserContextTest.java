package org.benchmarker.bmcontroller.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.benchmarker.bmcontroller.security.BMUserDetails;
import org.benchmarker.bmcontroller.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
public class UserContextTest {

    @InjectMocks
    private UserContext userContext;

    @Mock
    private Authentication authentication;

    @Mock
    private BMUserDetails userDetail;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증된 사용자가 getCurrentUser 를 호출하면 사용자 정보를 반환한다")
    public void testGetCurrentUser() {
        // given
        User user = new User();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        when(userDetail.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userDetail);

        // then
        assertEquals(user, userContext.getCurrentUser());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 getCurrentUser를 호출하면 AuthenticationCredentialsNotFoundException 예외가 발생한다")
    public void testGetCurrentUserWithoutAuthentication() {
        assertThrows(AuthenticationCredentialsNotFoundException.class,
            () -> userContext.getCurrentUser());
    }

    @Test
    @DisplayName("유효하지 않은 Principal 정보로 getCurrentUser를 호출하면 AuthenticationCredentialsNotFoundException 예외가 발생한다")
    public void testGetCurrentUserWithInvalidAuthentication() {
        // given
        Authentication invalidAuthentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(invalidAuthentication);

        // when
        when(invalidAuthentication.getPrincipal()).thenReturn("invalidPrincipal");

        // then
        assertThrows(AuthenticationCredentialsNotFoundException.class,
            () -> userContext.getCurrentUser());
    }
}
