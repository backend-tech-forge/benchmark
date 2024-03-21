package org.benchmarker.bmcontroller.user.service;

import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.security.BMUserDetails;
import org.benchmarker.bmcontroller.user.model.User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * Manage user information with {@link org.springframework.security.core.context.SecurityContext}
 */
@Component
@Slf4j
public class UserContext {

    /**
     * Get current user object from
     * {@link org.springframework.security.core.context.SecurityContext}
     *
     * @return {@link User}
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthenticationCredentialsNotFoundException("No authentication");
        }
        Object obj = auth.getPrincipal();
        if (!(obj instanceof BMUserDetails)) {
            throw new AuthenticationCredentialsNotFoundException(
                "Invalid authentication with " + obj);
        }
        BMUserDetails securedUser = (BMUserDetails) obj;
        return securedUser.getUser();
    }
}