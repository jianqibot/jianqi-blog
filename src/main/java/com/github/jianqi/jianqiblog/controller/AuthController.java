package com.github.jianqi.jianqiblog.controller;

import com.github.jianqi.jianqiblog.entity.AuthReturn;
import com.github.jianqi.jianqiblog.entity.User;
import com.github.jianqi.jianqiblog.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class AuthController {
    private static final String USERNAME_PATTERN = "[a-zA-Z\\d]{0,14}";
    private static final String PASSWORD_PATTERN = "[\\S]{5,15}";
    private static final Pattern PATTERN_USERNAME = Pattern.compile(USERNAME_PATTERN);
    private static final Pattern PATTERN_PASSWORD = Pattern.compile(PASSWORD_PATTERN);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Inject
    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/auth")
    @ResponseBody
    public AuthReturn auth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication == null ? null : authentication.getName();
        if (username == null || username.contains("anonymous")) {
            return AuthReturn.failureResult("ok", null);
        } else {
            return AuthReturn.successfulResult("ok", null, userService.getUserByUsername(username));
        }
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public AuthReturn login(@RequestBody Map<String, String> loginInfo) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        UserDetails userDetails;

        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return AuthReturn.failureResult("fail", "wrong username!");
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);
            User loggedInUser = userService.getUserByUsername(username);
            return AuthReturn.successfulResult("ok", "successfully logged in!", loggedInUser);
        } catch (BadCredentialsException e) {
            return AuthReturn.failureResult("fail", "wrong password!");
        }
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public AuthReturn register(@RequestBody Map<String, String> registerInfo) {
        String username = registerInfo.get("username");
        String password = registerInfo.get("password");

        if (isValidUsername(username) && isValidPassword(password)) {
            try {
                userService.saveUserNameAndPassword(username, password);
            } catch (DuplicateKeyException e) {
                e.printStackTrace();
                return AuthReturn.failureResult("fail", "username already exists!");
            }
            User registeredUser = userService.getUserByUsername(username);
            return AuthReturn.successfulResult("ok", "registry is successful", registeredUser);
        } else if (!isValidUsername(username)) {
            return AuthReturn.failureResult("fail", "invalid username!");
        } else if (!isValidPassword(password)) {
            return AuthReturn.failureResult("fail", "invalid password");
        }
        return null;
    }

    private boolean isValidPassword(String password) {
        return PATTERN_PASSWORD.matcher(password).matches();
    }

    private boolean isValidUsername(String username) {
        return PATTERN_USERNAME.matcher(username).matches();
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public AuthReturn logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return AuthReturn.failureResult("fail", "username has not logged in yet!");
        } else {
            SecurityContextHolder.clearContext();
            return AuthReturn.successfulResult("ok", "successfully logged out!", null);
        }
    }
}
