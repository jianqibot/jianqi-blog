package com.github.jianqi.jianqiblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jianqi.jianqiblog.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void SetUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, authenticationManager)).build();
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse()
                        .getContentAsString().contains("\"status\":\"ok\",\"msg\":null,\"user\":null,\"login\":false")));
    }

    @Test
    void testLoginAndLogout() throws Exception {
        Mockito.when(userService.loadUserByUsername("randomUser"))
                .thenReturn(new User("randomUser", encoder.encode("randomPassword"),
                        Collections.emptyList()));

        Mockito.when(userService.getUserByUsername("randomUser"))
                .thenReturn(new com.github.jianqi.jianqiblog.entity.User(
                        1, "randomUser", encoder.encode("randomPassword")));
        // before login
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse()
                        .getContentAsString().contains("\"status\":\"ok\",\"msg\":null,\"user\":null,\"login\":false")));

        // logging in
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "randomUser");
        userInfo.put("password", "randomPassword");

        MvcResult response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userInfo)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse()
                        .getContentAsString().contains("\"msg\":\"successfully logged in!\"")))
                .andReturn();

        // after login
        HttpSession session = response.getRequest().getSession();
        mvc.perform(get("/auth").session((MockHttpSession) Objects.requireNonNull(session)))
                .andExpect(status().isOk())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResponse().getContentAsString().contains("randomUser")));

        mvc.perform(get("/auth/logout").session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("successfully logged out!")));
    }

    @Test
    void newUserIsAbleToRegister() throws Exception {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "randomUser");
        userInfo.put("password", "randomPassword");

        Mockito.doNothing().when(userService).saveUserNameAndPassword("randomUser", "randomPassword");

        Mockito.when(userService.getUserByUsername("randomUser"))
                .thenReturn(new com.github.jianqi.jianqiblog.entity.User(1, "randomUser", "encryptedRandomPassword"));

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userInfo)))
                .andExpect(status().isOk())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResponse().getContentAsString()
                                .contains("\"msg\":\"registry is successful\"")));
    }
}
