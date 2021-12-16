package com.github.jianqi.jianqiblog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jianqi.jianqiblog.JianqiBlogApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JianqiBlogApplication.class, webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class IntegrationTest {
    @Inject
    Environment environment;

    @Test
    void integrationTest() throws IOException, InterruptedException {
        String port = environment.getProperty("local.server.port");
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "randomUser");
        userInfo.put("password", "randomPassword");

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
        HttpRequest requestBeforeLoggedIn = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth")).GET().build();
        HttpResponse<String> responseBeforeLoggedIn = client.send(requestBeforeLoggedIn, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content before logging in",
                () -> Assertions.assertEquals(200, responseBeforeLoggedIn.statusCode()),
                () -> Assertions.assertTrue(responseBeforeLoggedIn.body().contains("\"login\":false"))
        );


        HttpRequest requestToRegister = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/register"))
                .headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(userInfo)))
                .build();

        HttpResponse<String> responseToRegister = client.send(requestToRegister, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of registery",
                () -> Assertions.assertEquals(200, responseToRegister.statusCode()),
                () -> Assertions.assertTrue(responseToRegister.body().contains("registry is successful")));


        HttpRequest requestToLogin = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/login"))
                .headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(userInfo)))
                .build();

        HttpResponse<String> responseToLogin = client.send(requestToLogin, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of logging in",
                () -> Assertions.assertEquals(200, responseToLogin.statusCode()),
                () -> Assertions.assertTrue(responseToLogin.body().contains("successfully logged in!")));


        HttpRequest requestCheckIfLoggedIn = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth")).GET().build();
        HttpResponse<String> responseCheckIfLoggedIn = client.send(requestCheckIfLoggedIn, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after logging in",
                () -> Assertions.assertEquals(200, responseCheckIfLoggedIn.statusCode()),
                () -> Assertions.assertTrue(responseCheckIfLoggedIn.body().contains("\"login\":true"))
        );


        HttpRequest requestToLogout = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/logout")).GET().build();
        HttpResponse<String> responseToLogout = client.send(requestToLogout, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of logging out",
                () -> Assertions.assertEquals(200, responseToLogout.statusCode()),
                () -> Assertions.assertTrue(responseToLogout.body().contains("successfully logged out")));


        HttpRequest requestCheckIfLoggedOut = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth")).GET().build();
        HttpResponse<String> responseCheckIfLoggedOut = client.send(requestCheckIfLoggedOut, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after logging out",
                () -> Assertions.assertEquals(200, responseCheckIfLoggedOut.statusCode()),
                () -> Assertions.assertTrue(responseCheckIfLoggedOut.body().contains("\"login\":false"))
        );
    }
}
