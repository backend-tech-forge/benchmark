package org.benchmarker.bmagent.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

@Slf4j
public class WebClientSupport {
    public static RequestHeadersSpec<?> create(String method, String targetUrl, Object body,
        Map<String, String> headers) throws MalformedURLException {
        URL url = new URL(targetUrl);
        String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
        log.info(baseUrl);
        String resourcePath = url.getPath();
        log.info(resourcePath);
        WebClient webClient = WebClient.create(baseUrl);

        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        if (httpMethod == null) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        RequestHeadersSpec<?> request;
        if (httpMethod.equals(HttpMethod.GET)) {
            request = webClient.get().uri(resourcePath);
        } else if (httpMethod.equals(HttpMethod.POST)) {
            request = webClient.post().uri(resourcePath).bodyValue(body);
        } else if (httpMethod.equals(HttpMethod.PUT)) {
            request = webClient.put().uri(resourcePath).bodyValue(body);
        } else if (httpMethod.equals(HttpMethod.PATCH)) {
            request = webClient.patch().uri(resourcePath).bodyValue(body);
        } else if (httpMethod.equals(HttpMethod.DELETE)) {
            request = webClient.delete().uri(resourcePath);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        if (headers != null) {
            request.headers(h -> headers.forEach((key, value) -> h.add(key, value)));
        }

        return request;
    }
}