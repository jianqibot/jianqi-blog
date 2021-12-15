package com.github.jianqi.jianqiblog.service;

import com.github.jianqi.jianqiblog.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class AuthService {
    private final UserService userService;

    @Inject
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(userService.getUserByUsername(authentication == null ? null : authentication.getName()));
    }
}
