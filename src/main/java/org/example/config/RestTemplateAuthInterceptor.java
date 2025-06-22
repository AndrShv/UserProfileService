package org.example.config;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public class RestTemplateAuthInterceptor implements ClientHttpRequestInterceptor {

    private final String token;

    public RestTemplateAuthInterceptor(String token) {
        this.token = token;
    }

    @Override
    public org.springframework.http.client.ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add("Authorization", "Bearer " + token);
        return execution.execute(request, body);
    }
}

