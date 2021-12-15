package com.github.jianqi.jianqiblog.controller;

import com.github.jianqi.jianqiblog.entity.LoginResult;
import com.github.jianqi.jianqiblog.entity.User;
import com.github.jianqi.jianqiblog.service.AuthService;
import com.github.jianqi.jianqiblog.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AuthService authService;

    @Inject
    public AuthController(UserService userService, AuthenticationManager authenticationManager, AuthService authService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }


    @GetMapping("/auth")
    @ResponseBody
    public LoginResult auth() {
        return authService.getLoggedInUser().map(user ->
                LoginResult.success("ok", null, user, true))
                .orElse(LoginResult.failure("ok", null));
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public LoginResult login(@RequestBody Map<String, String> loginInfo) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        UserDetails userDetails;

        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return LoginResult.failure("fail", "wrong username!");
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);
            User loggedInUser = userService.getUserByUsername(username);
            return LoginResult.success("ok", "successfully logged in!", loggedInUser, true);
        } catch (BadCredentialsException e) {
            return LoginResult.failure("fail", "wrong password!");
        }
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public LoginResult register(@RequestBody Map<String, String> registerInfo) {
        String username = registerInfo.get("username");
        String password = registerInfo.get("password");

        if (isValidUsername(username) && isValidPassword(password)) {
            try {
                userService.saveUserNameAndPassword(username, password);
            } catch (DuplicateKeyException e) {
                e.printStackTrace();
                return LoginResult.failure("fail", "username already exists!");
            }
            User registeredUser = userService.getUserByUsername(username);
            return LoginResult.success("ok", "registry is successful", registeredUser, false);
        } else if (!isValidUsername(username)) {
            return LoginResult.failure("fail", "invalid username!");
        } else if (!isValidPassword(password)) {
            return LoginResult.failure("fail", "invalid password");
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
    public LoginResult logout() {
        LoginResult result = authService.getLoggedInUser()
                .map(user -> LoginResult.success("ok",
                        "successfully logged out",
                        null,
                        false))
                .orElse(LoginResult.failure("fail", "username has not logged in yet"));
        SecurityContextHolder.clearContext();
        return result;
    }
}
