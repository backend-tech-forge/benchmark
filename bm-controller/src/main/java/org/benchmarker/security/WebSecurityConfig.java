package org.benchmarker.security;


import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.model.User;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withDefaultPasswordEncoder().username("admin").password("admin").roles("USER","ADMIN").build());
//        manager.createUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
//        return manager;
//    }

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
                .formLogin(FormLoginConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/user/**").permitAll() // "/user/**" 경로는 USER 역할을 가진 사용자에게만 허용
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증된 사용자에게만 허용
                )
                .httpBasic(withDefaults());
        return http.build();
    }

}