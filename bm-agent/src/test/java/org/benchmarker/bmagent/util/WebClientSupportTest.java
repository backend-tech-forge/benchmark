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
        Map<String, String> header = Map.of("header", "headervalue");

        RequestHeadersSpec<?> requestHeadersSpec = WebClientSupport.create("GET",
            mockServer.url("/").toString(), "body", header);

        String response = requestHeadersSpec.retrieve().bodyToMono(String.class).block();

        requestHeadersSpec = WebClientSupport.create("POST",
            mockServer.url("/").toString(), "body", header);

        response = requestHeadersSpec.retrieve().bodyToMono(String.class).block();

        requestHeadersSpec = WebClientSupport.create("put",
            mockServer.url("/").toString(), "body", header);

        response = requestHeadersSpec.retrieve().bodyToMono(String.class).block();

        requestHeadersSpec = WebClientSupport.create("patch",
            mockServer.url("/").toString(), "body", header);

        response = requestHeadersSpec.retrieve().bodyToMono(String.class).block();

        requestHeadersSpec = WebClientSupport.create("delete",
            mockServer.url("/").toString(), "body", header);

        response = requestHeadersSpec.retrieve().bodyToMono(String.class).block();


    }

}