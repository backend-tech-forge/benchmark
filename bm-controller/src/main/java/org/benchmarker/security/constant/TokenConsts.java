package org.benchmarker.security.constant;

public interface TokenConsts {
    // cookie name for access token and refresh token
    String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    // Token payload has a key named "role" to store user's role
    String AUTHORITIES_KEY_NAME = "role";
}
