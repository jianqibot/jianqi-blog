package com.github.jianqi.jianqiblog.controller;

import com.github.jianqi.jianqiblog.entity.BlogListResult;
import com.github.jianqi.jianqiblog.entity.BlogResult;
import com.github.jianqi.jianqiblog.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
public class BlogController {
    private BlogService blogService;

    @Inject
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/blog")
    @ResponseBody
    public BlogListResult blog(@RequestParam("page") Integer page,
                               @RequestParam(value = "userId", required = false) Integer userId) {

        if (page == null || page < 0) {
            page = 1;
        }
        return blogService.getBlogs(page, 10, userId);
    }

    @GetMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult blogId(@PathVariable("blogId") Integer blogId) {
        return blogService.getBlogById(blogId);
    }
}
