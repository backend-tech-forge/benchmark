package org.benchmarker.bmcontroller.security;

import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.benchmarker.bmcontroller.user.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class BMUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public BMUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("loadByUsername : {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with userId: " + userId));

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
    }
}
