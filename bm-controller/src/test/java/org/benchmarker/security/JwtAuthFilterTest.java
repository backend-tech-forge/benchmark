package org.benchmarker.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import org.benchmarker.user.controller.constant.TestUserConsts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JwtAuthFilterTest {

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;
    @Mock
    private BMUserDetails userDetails;
    @Mock
    private BMUserDetailsService userDetailsService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("토큰이 유효한 경우 auth NotNull 체크")
    public void testDoFilterInternal_ValidToken() throws Exception {
        // given
        String userId = "testUser";
        when(jwtTokenProvider.validateTokenAndGetUserId(any(), any())).thenReturn(userId);
        when(userDetailsService.loadUserByUsername(userId)).thenReturn(userDetails);
        when(jwtTokenProvider.validateTokenAndGetUserId(any(), any())).thenReturn(userId);
        TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(TestUserConsts.id, TestUserConsts.password, "ROLE_USER");
        when(userDetails.getAuthorities())
            .thenReturn((Collection) authenticationToken.getAuthorities());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        jwtAuthFilter.doFilterInternal(request, response, (req, res) -> {
        });

        // then
        verify(userDetailsService, times(1)).loadUserByUsername(userId);
        verify(jwtTokenProvider, times(1)).validateTokenAndGetUserId(any(), any());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("토큰이 유효하지 않은 경우 auth null 체크")
    public void testDoFilterInternal_InvalidToken() throws Exception {
        // given
        when(jwtTokenProvider.validateTokenAndGetUserId(any(), any())).thenReturn(null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        jwtAuthFilter.doFilterInternal(request, response, (req, res) -> {
        });

        // then
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(jwtTokenProvider, times(1)).validateTokenAndGetUserId(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
