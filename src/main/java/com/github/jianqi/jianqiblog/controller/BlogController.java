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
import java.util.Map;

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
    public BlogListResult getBlogs(@RequestParam("page") Integer page,
                                   @RequestParam(value = "userId", required = false) Integer userId) {
        if (page == null || page < 0) {
            page = 1;
        }
        return blogService.getBlogs(page, 10, userId);
    }

    @GetMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult getBlogById(@PathVariable("blogId") Integer blogId) {
        return blogService.getBlogById(blogId);
    }

    @PostMapping("/blog")
    @ResponseBody
    public BlogResult postBlog(@RequestBody Map<String, String> params) {

        return authService.getLoggedInUser()
                .map(user -> blogService.postBlog(formBlogFromParams(params, user)))
                .orElse(BlogResult.failure("fail", "log in first"));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public BlogResult handleIllegalArgumentException(IllegalArgumentException e) {
        return BlogResult.failure("fail", e.getMessage());
    }

    @PatchMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult postBlog(@PathVariable("blogId") Integer blogId,
                               @RequestBody Map<String, String> params) {

        return authService.getLoggedInUser()
                .map(user -> blogService.updateBlog(blogId, formBlogFromParams(params, user)))
                .orElse(BlogResult.failure("fail", "log in first"));
    }

    @DeleteMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult deleteBlog(@PathVariable("blogId") Integer blogId) {

        return authService.getLoggedInUser()
                .map(user -> blogService.deleteBlog(blogId, user.getId()))
                .orElse(BlogResult.failure("fail", "log in first"));
    }

    private Blog formBlogFromParams(Map<String, String> params, User user) {

        String title = params.get("title");
        String content = params.get("content");
        String description = params.get("description");

        if (title.isBlank() || title.length() > 100) {
            throw new IllegalArgumentException("invalid title");
        } else if (content.isBlank() || content.length() > 10000) {
            throw new IllegalArgumentException("invalid content");
        }

        if (description.isBlank()) {
            description = content.substring(0, Math.min(20, content.length()));
        }

        Blog formedBlog = new Blog();
        formedBlog.setTitle(title);
        formedBlog.setContent(content);
        formedBlog.setDescription(description);
        formedBlog.setUserId(user.getId());
        return formedBlog;
    }
}
