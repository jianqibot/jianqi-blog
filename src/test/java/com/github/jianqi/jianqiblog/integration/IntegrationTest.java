package com.github.jianqi.jianqiblog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jianqi.jianqiblog.JianqiBlogApplication;
import org.junit.jupiter.api.*;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {
    @Inject
    private Environment environment;
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
    private String port;

    private Map<String, String> getUserInfo() {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "randomUser");
        userInfo.put("password", "randomPassword");
        return userInfo;
    }

    @BeforeAll
    void setup() {
        port = environment.getProperty("local.server.port");
    }

    @Test
    @Order(1)
    void returnFailureWhenNotLoggedIn() throws IOException, InterruptedException {

        HttpRequest requestBeforeLoggedIn = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth")).GET().build();
        HttpResponse<String> responseBeforeLoggedIn = client.send(requestBeforeLoggedIn, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content before logging in",
                () -> Assertions.assertEquals(200, responseBeforeLoggedIn.statusCode()),
                () -> Assertions.assertTrue(responseBeforeLoggedIn.body().contains("\"login\":false"))
        );
    }

    @Test
    @Order(2)
    void newUserIsAbleToRegister() throws IOException, InterruptedException {

        HttpRequest requestToRegister = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/register"))
                .headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(getUserInfo())))
                .build();

        HttpResponse<String> responseToRegister = client.send(requestToRegister, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of registery",
                () -> Assertions.assertEquals(200, responseToRegister.statusCode()),
                () -> Assertions.assertTrue(responseToRegister.body().contains("registry is successful")));
    }

    @Test
    @Order(3)
    void registeredUserIsAbleToLogin() throws IOException, InterruptedException {
        HttpRequest requestToLogin = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/login"))
                .headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(getUserInfo())))
                .build();

        HttpResponse<String> responseToLogin = client.send(requestToLogin, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of logging in",
                () -> Assertions.assertEquals(200, responseToLogin.statusCode()),
                () -> Assertions.assertTrue(responseToLogin.body().contains("successfully logged in!")));
    }

    @Test
    @Order(4)
    void returnSuccessWhenLoggedIn() throws IOException, InterruptedException {
        HttpRequest requestCheckIfLoggedIn = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth")).GET().build();
        HttpResponse<String> responseCheckIfLoggedIn = client.send(requestCheckIfLoggedIn, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after logging in",
                () -> Assertions.assertEquals(200, responseCheckIfLoggedIn.statusCode()),
                () -> Assertions.assertTrue(responseCheckIfLoggedIn.body()
                        .contains("\"login\":true")));
    }

    @Test
    @Order(5)
    void returnSuccessWhenLogoutAfterLogin() throws IOException, InterruptedException {
        HttpRequest requestToLogout = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/logout")).GET().build();
        HttpResponse<String> responseToLogout = client.send(requestToLogout, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of logging out",
                () -> Assertions.assertEquals(200, responseToLogout.statusCode()),
                () -> Assertions.assertTrue(responseToLogout.body()
                        .contains("successfully logged out")));
    }

    @Test
    @Order(6)
    void returnFailureAfterLogout() throws IOException, InterruptedException {
        HttpRequest requestCheckIfLoggedOut = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth")).GET().build();
        HttpResponse<String> responseCheckIfLoggedOut = client.send(requestCheckIfLoggedOut, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after logging out",
                () -> Assertions.assertEquals(200, responseCheckIfLoggedOut.statusCode()),
                () -> Assertions.assertTrue(responseCheckIfLoggedOut.body()
                        .contains("\"login\":false")));
    }
}
