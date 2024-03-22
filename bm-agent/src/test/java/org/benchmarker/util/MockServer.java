package org.benchmarker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * webClient 요청을 받을 mock 서버를 생성합니다
 */
public class MockServer {

    /**
     * Mock server instance
     */
    public static MockWebServer mockServer;
    /**
     * Opened mock server url
     */
    public String backendUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        // webClient 요청을 받을 mock 서버를 생성합니다
        mockServer = new MockWebServer();
        mockServer.start();
        backendUrl = String.format(mockServer.url("/").toString());
        System.out.println("Mock server url: " + backendUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Mock server에 응답을 추가합니다
     *
     * <p> 만약 Object 를 Json String 으로 변환할 수 없다면 "" 값이 응답으로 반환됩니다</p>
     *
     * @param object
     */
    public void addMockResponse(Object object) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        MockResponse response = new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(json);
        mockServer.enqueue(response);
    }

    /**
     * Mock server에 응답을 추가합니다. 반복 횟수를 지정할 수 있습니다.
     *
     * <p> 만약 Object 를 Json String 으로 변환할 수 없다면 "" 값이 응답으로 반환됩니다</p>
     *
     * @param object
     * @param repeatCount
     */
    public void addMockResponse(Object object, int repeatCount) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        MockResponse response = new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(json);
        for (int i = 0; i < repeatCount; i++) {
            mockServer.enqueue(response);
        }
    }
}
