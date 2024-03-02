package org.benchmarker.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.security.dto.LoginRequestInfo;
import org.benchmarker.user.model.User;
import org.benchmarker.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * login method is used to authenticate user and create tokens.
     *
     * @param req {@link LoginRequestInfo}
     * @return Token String
     */
    @Transactional
    public String login(LoginRequestInfo req) {
        User user = userRepository.findById(req.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
    }

}
