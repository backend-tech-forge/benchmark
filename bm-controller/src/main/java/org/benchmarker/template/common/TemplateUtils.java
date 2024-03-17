package org.benchmarker.template.common;

import org.benchmarker.template.model.TestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Locale;

public class TemplateUtils {

    public static double calculateTPS(long startTime, long endTime, int totalRequests) {
        // 경과 시간 계산 (밀리초 단위)
        long elapsedTime = endTime - startTime;

        // 경과 시간이 0보다 작거나 같으면 0을 반환하여 나누기 오류를 방지합니다.
        if (elapsedTime <= 0) {
            return 0;
        }

        // 초당 트랜잭션 수 계산 (총 요청 수를 경과 시간(초)으로 나눔)
        return (double) totalRequests / (elapsedTime / 1000.0);
    }

    public static double calculateAvgResponseTime(long startTime, long endTime, int totalRequests) {
        // 경과 시간 계산 (밀리초 단위)
        long elapsedTime = endTime - startTime;

        // 경과 시간이 0보다 작거나 같으면 0을 반환하여 나누기 오류를 방지합니다.
        if (elapsedTime <= 0 || totalRequests == 0) {
            return 0;
        }

        // 평균 응답 시간 계산 (총 경과 시간을 총 요청 수로 나눔)
        return (double) elapsedTime / totalRequests;
    }

    public static Mono<ResponseEntity<String>> createRequest(WebClient webClient, TestTemplate testTemplate) {
        String method = testTemplate.getMethod().toUpperCase(Locale.ENGLISH);
        return switch (method) {
            case "GET" -> webClient.get()
                    .uri(testTemplate.getUrl())
                    .retrieve()
                    .toEntity(String.class);
            case "POST" -> webClient.post()
                    .uri(testTemplate.getUrl())
                    .bodyValue(testTemplate.getBody())
                    .retrieve()
                    .toEntity(String.class);
            case "PATCH", "PUT" -> webClient.patch()
                    .uri(testTemplate.getUrl())
                    .bodyValue(testTemplate.getBody())
                    .retrieve()
                    .toEntity(String.class);
            case "DELETE" -> webClient.delete()
                    .uri(testTemplate.getUrl())
                    .retrieve()
                    .toEntity(String.class);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }
}
