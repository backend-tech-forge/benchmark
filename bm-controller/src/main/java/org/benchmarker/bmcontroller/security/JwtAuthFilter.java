package org.benchmarker.bmcontroller.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.security.constant.TokenConsts;
import org.benchmarker.bmcontroller.security.constant.URLConsts;
import org.benchmarker.bmcontroller.security.util.MethodUrlPair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final BMUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(BMUserDetailsService userDetailsService,
        JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        for (MethodUrlPair methodUrlPair : URLConsts.WHITE_LIST_URLS) {
            if (methodUrlPair.getMethod().contains(request.getMethod()) &&
                methodUrlPair.getUrl().equals(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            String userId = jwtTokenProvider.validateTokenAndGetUserId(request,
                TokenConsts.ACCESS_TOKEN_COOKIE_NAME);
            if (userId != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (UsernameNotFoundException ex) {
            Cookie cookie = new Cookie(TokenConsts.ACCESS_TOKEN_COOKIE_NAME, null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            response.sendRedirect("/");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
