package com.github.jianqi.jianqiblog.integration;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.http.HttpRequest;

public interface HttpRequestBuilder {
    HttpRequest buildRequest(String port, String api, String method, Object requestBody) throws JsonProcessingException;
}
