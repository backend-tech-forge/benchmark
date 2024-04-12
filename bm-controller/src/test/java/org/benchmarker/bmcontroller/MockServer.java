package org.benchmarker.bmcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;

/**
 * webClient 요청을 받을 mock 서버를 생성합니다
 */
public class MockServer {

    /**
     * Mock server instance
     */
    public static MockWebServer mockBackEnd;
    /**
     * Opened mock server url
     */
    public String backendUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        // webClient 요청을 받을 mock 서버를 생성합니다
        System.out.println("start mock");
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        backendUrl = String.format(mockBackEnd.url("/").toString());
        System.out.println("Mock server url: " + backendUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
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
        mockBackEnd.enqueue(response);
    }

    /**
     * Mock server에 응답을 추가합니다. 반복 횟수를 지정할 수 있습니다.
     *
     * <p> 만약 Object 를 Json String 으로 변환할 수 없다면 "" 값이 응답으로 반환됩니다</p>
     *
     * @param object
     * @param repeat
     */
    public void addMockResponse(Object object, int repeat) {
        String json = "";
        try {
            json = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        MockResponse response = new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(json);
        for (int i = 0; i < repeat; i++) {
            mockBackEnd.enqueue(response);
        }
    }

    /**
     * SSE 이벤트를 응답에 추가합니다.
     *
     * @param eventData
     */
    public void addMockResponseSSE(Object eventData) {
        String jsonEventData = toJson(eventData);

        MockResponse response = new MockResponse()
            .addHeader("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
            .setBody("data: " + jsonEventData + "\n\n");

        mockBackEnd.enqueue(response);
    }

    /**
     * SSE 이벤트를 응답에 추가합니다. 반복횟수를 지정할 수 있습니다.
     * 
     * @param eventData
     * @param repeat
     */
    public void addMockResponseSSE(Object eventData, int repeat) {
        String jsonEventData = toJson(eventData);

        MockResponse response = new MockResponse()
            .addHeader("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
            .setBody("data: " + jsonEventData + "\n\n");

        for (int i = 0; i < repeat; i++) {
            mockBackEnd.enqueue(response);
        }
    }

    private String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
