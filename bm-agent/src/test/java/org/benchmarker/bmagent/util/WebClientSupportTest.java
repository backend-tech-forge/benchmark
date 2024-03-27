package org.benchmarker.bmagent.util;

import java.net.MalformedURLException;
import java.util.Map;
import org.benchmarker.util.MockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

class WebClientSupportTest extends MockServer {

    @Test
    @DisplayName("WebClient RequestHeaders 생성 테스트")
    void test1() throws MalformedURLException {

        addMockResponse("success",5);

        RequestHeadersSpec<?> requestHeadersSpec = WebClientSupport.create("GET",
            mockServer.url("/").toString(), "body", Map.of("header", "headervalue"));

        String response = requestHeadersSpec.retrieve().bodyToMono(String.class).block();

        requestHeadersSpec = WebClientSupport.create("POST",
            mockServer.url("/").toString(), "body", Map.of("header", "headervalue"));

        response = requestHeadersSpec.retrieve().bodyToMono(String.class).block();


    }

}