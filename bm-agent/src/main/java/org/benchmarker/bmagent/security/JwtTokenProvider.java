package org.benchmarker.bmagent.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * {@link JwtTokenProvider} class is used to create Json Web Token.
 */
@Slf4j
@Component
@Setter
public class JwtTokenProvider {

    /**
     * Access token's expiration time
     */
    public static String expirationTime;

    /**
     * Refresh token's expiration time
     */
    public static String refreshExpirationTime;

    /**
     * Secret key for signing the token
     */
    public static String secret;

    @Value("${token.expiration_time}")
    private void setExpirationTime(String expirationTime) {
        JwtTokenProvider.expirationTime = expirationTime;
    }

    @Value("${token.refresh_expiration_time}")
    private void setRefreshExpirationTime(String refreshExpirationTime) {
        JwtTokenProvider.refreshExpirationTime = refreshExpirationTime;
    }

    @Value("${token.secret}")
    private void setSecret(String secret) {
        JwtTokenProvider.secret = secret;
    }


    /**
     * Create Json Web Token with user's username and authorities
     *
     * <p> Here, this method generate accessToken </p>
     * <p> JWT's payload will looks like below</p>
     * <blockquote><pre>
     *  {
     *      "sub": "agent"
     *  }
     * </pre></blockquote>
     *
     * @return Json-web-token
     */
    public static String createAccessToken() {
        Claims claims = Jwts.claims().setSubject("agent");

        Long expirationTimeLong = Long.parseLong(expirationTime);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);
        return Jwts.builder()
            .setClaims(claims)
            .setSubject("agent")
            .setIssuedAt(createdDate)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
}
