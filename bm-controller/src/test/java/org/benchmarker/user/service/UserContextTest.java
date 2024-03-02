package org.benchmarker.user.service;

import org.benchmarker.security.BMUserDetails;
import org.benchmarker.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserContextTest {

    @MockBean
    private UserContext userContext;

    @Test
    @DisplayName("인증된 사용자가 getCurrentUser를 호출하면 사용자 정보를 반환한다")
    public void testGetCurrentUser() {
        // given
        User user = new User();
        Authentication authentication = mock(Authentication.class);
        BMUserDetails userDetail = mock(BMUserDetails.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        when(userDetail.getUser()).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userDetail);
        when(userContext.getCurrentUser()).thenCallRealMethod();

        // then
        assertEquals(user, userContext.getCurrentUser());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 getCurrentUser를 호출하면 AuthenticationCredentialsNotFoundException 예외가 발생한다")
    public void testGetCurrentUserWithoutAuthentication() {
        // when
        when(userContext.getCurrentUser()).thenCallRealMethod();

        // then
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userContext.getCurrentUser());
    }

    @Test
    @DisplayName("유효하지 않은 Principal 정보로 getCurrentUser를 호출하면 AuthenticationCredentialsNotFoundException 예외가 발생한다")
    public void testGetCurrentUserWithInvalidAuthentication() {
        // Mocking Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("invalidPrincipal");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mocking UserContext
        when(userContext.getCurrentUser()).thenCallRealMethod();

        // Testing
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userContext.getCurrentUser());
    }
}
