package com.github.jianqi.jianqiblog.service;

import com.github.jianqi.jianqiblog.entity.User;
import com.github.jianqi.jianqiblog.dao.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private BCryptPasswordEncoder mockPasswordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void saveUserNameAndPassword() {
        when(mockPasswordEncoder.encode("password")).thenReturn("encodedPassword");
        userService.saveUserNameAndPassword("username", "password");
        verify(mockUserMapper).saveUserInfo("username", "encodedPassword");
    }

    @Test
    void getUserByUsername() {
        userService.getUserByUsername("username");
        verify(mockUserMapper).findUserByUsername("username");
    }

    @Test
    @DisplayName("loadUserByUsername user not found")
    void throwExceptionWhenUserNotFound() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("username"));
    }

    @Test
    @DisplayName("loadUserByUsername user found")
    void returnUserDetailsWhenUserFound() {
        when(mockUserMapper.findUserByUsername("username"))
                .thenReturn(new User(1, "username", "encodedPassword"));
        UserDetails userDetails = userService.loadUserByUsername("username");
        Assertions.assertAll("user info",
                () -> Assertions.assertEquals("username", userDetails.getUsername()),
                () -> Assertions.assertEquals("encodedPassword", userDetails.getPassword()));
    }
}
