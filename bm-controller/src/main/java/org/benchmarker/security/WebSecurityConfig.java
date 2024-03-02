package org.benchmarker.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.model.User;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // for @PreAuthorize
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final BMUserDetailsService BMUserDetailsService;

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            log.info("Finduser : {}",username);
            Optional<User> findUser = userRepository.findById(username);
            if(findUser.isEmpty()){
                return null;
            }

            User user = findUser.get();

            BMUserDetails userDetails = new BMUserDetails();
            userDetails.setUser(user);
            userDetails.setUsername(user.getId());
            userDetails.setPassword(user.getPassword());
            userDetails.setEnabled(true);
            userDetails.setAccountNonExpired(true);
            userDetails.setCredentialsNonExpired(true);
            userDetails.setAccountNonLocked(true);
            userDetails.setPermission(user.getRole().name());
            return userDetails;
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable) // disable form login
                .logout(AbstractHttpConfigurer::disable) // disable default logout
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(new JwtAuthFilter(BMUserDetailsService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint(new BMAuthenticationEntryPoint())
                .accessDeniedHandler(new BMAccessDeniedHandler())
        );
        return http.build();
    }

}