package com.github.jianqi.jianqiblog.controller;

import com.github.jianqi.jianqiblog.dao.UserMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {
    private final UserMapper userMapper;

    public IndexController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    public String index() {
        return "index.html";
    }
}
