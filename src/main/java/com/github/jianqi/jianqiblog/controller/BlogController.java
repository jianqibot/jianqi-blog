package com.github.jianqi.jianqiblog.controller;

import com.github.jianqi.jianqiblog.entity.Blog;
import com.github.jianqi.jianqiblog.entity.BlogListResult;
import com.github.jianqi.jianqiblog.entity.BlogResult;
import com.github.jianqi.jianqiblog.entity.User;
import com.github.jianqi.jianqiblog.service.AuthService;
import com.github.jianqi.jianqiblog.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Controller
public class BlogController {
    private final BlogService blogService;
    private final AuthService authService;

    @Inject
    public BlogController(BlogService blogService, AuthService authService) {
        this.blogService = blogService;
        this.authService = authService;
    }

    @GetMapping("/blog")
    @ResponseBody
    public BlogListResult getBlog(@RequestParam("page") Integer page,
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

    @PostMapping("/blog")
    @ResponseBody
    public BlogResult postBlog(@RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("content") String content) {

        if (title == null || title.isBlank() || title.length() > 100) {
            return BlogResult.failure("fail", "invalid title");
        } else if (content == null || content.isBlank() || content.length() > 10000) {
            return BlogResult.failure("fail", "invalid content");
        }

        if (description == null || description.isBlank()) {
            description = content.substring(0, Math.min(20, content.length()));
        }

        String finalDescription = description;
        return authService.getLoggedInUser()
                .map(user -> blogService.postBlog(formBlogFromParams(title, finalDescription, content, user)))
                .orElse(BlogResult.failure("fail", "log in first"));
    }

    private Blog formBlogFromParams(String title, String description, String content, User user) {
        Blog formedBlog = new Blog();
        formedBlog.setTitle(title);
        formedBlog.setContent(content);
        formedBlog.setDescription(description);
        formedBlog.setUserId(user.getId());
        return formedBlog;
    }
}
