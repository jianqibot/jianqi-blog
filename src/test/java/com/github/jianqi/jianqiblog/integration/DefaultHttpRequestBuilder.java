package com.github.jianqi.jianqiblog.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpRequest;

public class DefaultHttpRequestBuilder implements HttpRequestBuilder {

    private final ObjectMapper mapper;

    public DefaultHttpRequestBuilder() {
        mapper = new ObjectMapper();
    }

    @Override
    public HttpRequest buildRequest(String port, String api, String method, Object requestBody) throws JsonProcessingException {

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + api))
                .headers("Content-Type", "application/json;charset=UTF-8");

        switch (method) {
            case "get":
                return requestBuilder.GET().build();
            case "post":
                return requestBuilder.POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody))).build();
            case "patch":
            case "put":
                return requestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody))).build();
            case "delete":
                return requestBuilder.DELETE().build();
            default:
                return null;
        }
    }
}
