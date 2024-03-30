package org.benchmarker.bmagent.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup(){
        jwtTokenProvider = new JwtTokenProvider();
    }

    @Test
    @DisplayName("token creation")
    void test1(){
        String accessToken = JwtTokenProvider.createAccessToken();
    }
}