package com.github.jianqi.jianqiblog.integration;

import com.github.jianqi.jianqiblog.JianqiBlogApplication;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JianqiBlogApplication.class, webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {
    @Inject
    private Environment environment;
    private String port;
    private final HttpRequestBuilder httpRequestBuilder = new DefaultHttpRequestBuilder();
    private final HttpClient client;
    private final Map<String, String> userInfo;
    private final Map<String, String> blogInfo;
    private final Map<String, String> blogInfoForUpdate;

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    public IntegrationTest() {
        client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
        userInfo = new HashMap<>();
        blogInfo = new HashMap<>();
        blogInfoForUpdate = new HashMap<>();
    }

    @BeforeAll
    void setup() {
        // flyway:clean && flyway:migrate
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();

        port = environment.getProperty("local.server.port");
        userInfo.put("username", "randomUser");
        userInfo.put("password", "randomPassword");
        blogInfo.put("title", "randomTitle");
        blogInfo.put("content", "randomContent");
        blogInfo.put("description", "randomDescription");
        blogInfoForUpdate.put("title", "updateTitle");
        blogInfoForUpdate.put("content", "updateContent");
        blogInfoForUpdate.put("description", "updateDescription");
    }

    @Test
    @Order(1)
    void returnFailureWhenNotLoggedIn() throws IOException, InterruptedException {

        HttpRequest requestBeforeLoggedIn = httpRequestBuilder.buildRequest(port, "/auth", "get", null);
        HttpResponse<String> responseBeforeLoggedIn = client.send(requestBeforeLoggedIn, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content before logging in",
                () -> Assertions.assertEquals(200, responseBeforeLoggedIn.statusCode()),
                () -> Assertions.assertTrue(responseBeforeLoggedIn.body().contains("\"login\":false"))
        );
    }

    @Test
    @Order(2)
    void newUserIsAbleToRegister() throws IOException, InterruptedException {

        HttpRequest requestToRegister = httpRequestBuilder.buildRequest(port, "/auth/register", "post", userInfo);
        HttpResponse<String> responseToRegister = client.send(requestToRegister, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of registry",
                () -> Assertions.assertEquals(200, responseToRegister.statusCode()),
                () -> Assertions.assertTrue(responseToRegister.body().contains("registry is successful")));
    }

    @Test
    @Order(3)
    void registeredUserIsAbleToLogin() throws IOException, InterruptedException {
        HttpRequest requestToLogin = httpRequestBuilder.buildRequest(port, "/auth/login", "post", userInfo);
        HttpResponse<String> responseToLogin = client.send(requestToLogin, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of logging in",
                () -> Assertions.assertEquals(200, responseToLogin.statusCode()),
                () -> Assertions.assertTrue(responseToLogin.body().contains("successfully logged in!")));
    }

    @Test
    @Order(4)
    void returnSuccessWhenLoggedIn() throws IOException, InterruptedException {
        HttpRequest requestCheckIfLoggedIn = httpRequestBuilder.buildRequest(port, "/auth", "get", null);
        HttpResponse<String> responseCheckIfLoggedIn = client.send(requestCheckIfLoggedIn, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after logging in",
                () -> Assertions.assertEquals(200, responseCheckIfLoggedIn.statusCode()),
                () -> Assertions.assertTrue(responseCheckIfLoggedIn.body()
                        .contains("\"login\":true")));
    }

    @Test
    @Order(5)
    void loggedInUserIsAbleToCreateBlog() throws IOException, InterruptedException {

        HttpRequest requestToCreateNewBlog = httpRequestBuilder.buildRequest(port, "/blog", "post", blogInfo);
        HttpResponse<String> responseFromCreateNewBlog = client.send(requestToCreateNewBlog, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after trying to create new blog",
                () -> Assertions.assertEquals(200, responseFromCreateNewBlog.statusCode()),
                () -> Assertions.assertTrue(responseFromCreateNewBlog.body()
                        .contains("blog created successfully")));
    }

    @Test
    @Order(6)
    void userIsAbleToAccessAllBlogs() throws IOException, InterruptedException {

        HttpRequest requestToAccessAllBlogs = httpRequestBuilder.buildRequest(port, "/blog?page=1&atIndex=true", "get", null);
        HttpResponse<String> responseFromAccessAllBlogs = client.send(requestToAccessAllBlogs, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after trying to access all blogs",
                () -> Assertions.assertEquals(200, responseFromAccessAllBlogs.statusCode()),
                () -> Assertions.assertTrue(responseFromAccessAllBlogs.body()
                        .contains("acquirement successful")));
    }

    @Test
    @Order(7)
    void userIsAbleToAccessAnyParticularBlogs() throws IOException, InterruptedException {

        HttpRequest requestToAccessOneBlog = httpRequestBuilder.buildRequest(port, "/blog/1", "get", null);
        HttpResponse<String> responseFromAccessOneBlog = client.send(requestToAccessOneBlog, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after trying to access one blog in detail",
                () -> Assertions.assertEquals(200, responseFromAccessOneBlog.statusCode()),
                () -> Assertions.assertTrue(responseFromAccessOneBlog.body()
                        .contains("acquirement successful")));
    }

    @Test
    @Order(8)
    void loggedInUserIsAbleToUpdateHisBlogs() throws IOException, InterruptedException {

        HttpRequest requestToUpdateExistingBlog = httpRequestBuilder.buildRequest(port, "/blog/7", "patch", blogInfoForUpdate);
        HttpResponse<String> responseFromUpdateBlog = client.send(requestToUpdateExistingBlog, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after trying to access one blog in detail",
                () -> Assertions.assertEquals(200, responseFromUpdateBlog.statusCode()),
                () -> Assertions.assertTrue(responseFromUpdateBlog.body()
                        .contains("blog modified successfully")));
    }

    @Test
    @Order(9)
    void loggedInUserIsAbleToDeleteHisBlogs() throws IOException, InterruptedException {

        HttpRequest requestToDeleteExistingBlog = httpRequestBuilder.buildRequest(port, "/blog/7", "delete", null);
        HttpResponse<String> responseFromDeleteBlog = client.send(requestToDeleteExistingBlog, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after trying to access one blog in detail",
                () -> Assertions.assertEquals(200, responseFromDeleteBlog.statusCode()),
                () -> Assertions.assertTrue(responseFromDeleteBlog.body()
                        .contains("blog deleted successfully")));
    }

    @Test
    @Order(10)
    void returnSuccessWhenLogoutAfterLogin() throws IOException, InterruptedException {
        HttpRequest requestToLogout = httpRequestBuilder.buildRequest(port, "/auth/logout", "get", null);
        HttpResponse<String> responseToLogout = client.send(requestToLogout, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content of logging out",
                () -> Assertions.assertEquals(200, responseToLogout.statusCode()),
                () -> Assertions.assertTrue(responseToLogout.body()
                        .contains("successfully logged out")));
    }

    @Test
    @Order(11)
    void returnFailureAfterLogout() throws IOException, InterruptedException {
        HttpRequest requestCheckIfLoggedOut = httpRequestBuilder.buildRequest(port, "/auth", "get", null);
        HttpResponse<String> responseCheckIfLoggedOut = client.send(requestCheckIfLoggedOut, HttpResponse.BodyHandlers.ofString());

        Assertions.assertAll("check status and content after logging out",
                () -> Assertions.assertEquals(200, responseCheckIfLoggedOut.statusCode()),
                () -> Assertions.assertTrue(responseCheckIfLoggedOut.body()
                        .contains("\"login\":false")));
    }
}
