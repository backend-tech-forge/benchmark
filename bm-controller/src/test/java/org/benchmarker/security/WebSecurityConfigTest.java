package org.benchmarker.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.benchmarker.user.model.Role;
import org.benchmarker.user.model.User;
import org.benchmarker.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
class WebSecurityConfigTest {

    @InjectMocks
    private BMUserDetailsService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자가 존재하는 경우 UserDetailsService 테스트")
    public void testUserDetailsService_UserExists() {
        // given
        String username = "testUsername";
        User user = new User();
        user.setId(username);
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = userService.loadUserByUsername(username);

        // then
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 경우 UserDetailsService 테스트")
    public void testUserDetailsService_UserDoesNotExist() {
        // given
        String username = "nonexistentUsername";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername(username));
    }
}