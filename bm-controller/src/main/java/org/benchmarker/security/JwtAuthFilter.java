package org.benchmarker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.benchmarker.security.constant.TokenConsts.ACCESS_TOKEN_COOKIE_NAME;

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
        String userId = jwtTokenProvider.validateTokenAndGetUserId(request,
            ACCESS_TOKEN_COOKIE_NAME);
        if (userId != null) {
            log.info("userId : {}", userId);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("SecurityContextHolder.getContext().getAuthentication() : {}",
                SecurityContextHolder.getContext().getAuthentication());
        }

        filterChain.doFilter(request, response);
    }
}
