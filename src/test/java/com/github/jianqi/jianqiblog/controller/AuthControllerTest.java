package com.github.jianqi.jianqiblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jianqi.jianqiblog.entity.User;
import com.github.jianqi.jianqiblog.service.AuthService;
import com.github.jianqi.jianqiblog.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    @Mock
    private AuthService authService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void SetUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, authenticationManager, authService)).build();
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse()
                        .getContentAsString().contains("\"login\":false")));
    }

    @Test
    void returnSuccessAfterLogin() throws Exception {
        User user = new User(1, "randomUser", encoder.encode("randomPassword"));
        Mockito.when(authService.getLoggedInUser()).thenReturn(Optional.of(user));
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("\"login\":true")));
        System.out.println(user);;
    }


    @Test
    void registeredUserIsAbleToLogin() throws Exception {
        Mockito.when(userService.loadUserByUsername("randomUser"))
                .thenReturn(new org.springframework.security.core.userdetails.User("randomUser", encoder.encode("randomPassword"),
                        Collections.emptyList()));

        Mockito.when(userService.getUserByUsername("randomUser"))
                .thenReturn(new User(
                        1, "randomUser", encoder.encode("randomPassword")));

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
    }

    @Test
    void newUserIsAbleToRegister() throws Exception {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "randomUser");
        userInfo.put("password", "randomPassword");

        Mockito.doNothing().when(userService).saveUserNameAndPassword("randomUser", "randomPassword");

        Mockito.when(userService.getUserByUsername("randomUser"))
                .thenReturn(new User(1, "randomUser", "encryptedRandomPassword"));

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userInfo)))
                .andExpect(status().isOk())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResponse().getContentAsString()
                                .contains("\"msg\":\"registry is successful\"")));
    }

    @Test
    void logOutReturnFailureByDefault() throws Exception {
        mvc.perform(get("/auth/logout")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse()
                        .getContentAsString().contains("\"msg\":\"username has not logged in yet\"")));
    }

    @Test
    void logOutReturnSuccessWhenLoggedIn() throws Exception {
        User user = new User(1, "randomUser", encoder.encode("randomPassword"));
        Mockito.when(authService.getLoggedInUser()).thenReturn(Optional.of(user));
        mvc.perform(get("/auth/logout")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse()
                        .getContentAsString().contains("\"msg\":\"successfully logged out\"")));
    }
}
