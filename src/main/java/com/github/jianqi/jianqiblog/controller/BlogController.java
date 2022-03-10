package com.github.jianqi.jianqiblog.controller;

import com.github.jianqi.jianqiblog.entity.Blog;
import com.github.jianqi.jianqiblog.entity.BlogListResult;
import com.github.jianqi.jianqiblog.entity.BlogResult;
import com.github.jianqi.jianqiblog.entity.User;
import com.github.jianqi.jianqiblog.service.AuthService;
import com.github.jianqi.jianqiblog.service.BlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@Api(value = "manage operation on blogs", tags = "BlogController")
@Controller
public class BlogController {
    private final BlogService blogService;
    private final AuthService authService;

    @Inject
    public BlogController(BlogService blogService, AuthService authService) {
        this.blogService = blogService;
        this.authService = authService;
    }


    @ApiOperation("Fetch all blogs")
    @GetMapping("/blog")
    @ResponseBody
    public BlogListResult getBlogs(@RequestParam("page") @ApiParam("Page Number")
                                               Integer page,
                                   @RequestParam(value = "userId", required = false) @ApiParam("User Id")
                                           Integer userId) {
        if (page == null || page < 0) {
            page = 1;
        }
        return blogService.getBlogs(page, 10, userId);
    }


    @ApiOperation("Fetch blog of specific Id")
    @GetMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult getBlogById(@PathVariable("blogId") @ApiParam("Blog Id") Integer blogId) {
        return blogService.getBlogById(blogId);
    }


    @ApiOperation("Create new blog")
    @PostMapping("/blog")
    @ResponseBody
    public BlogResult postBlog(@RequestBody @ApiParam("Blog Information(title, content...) to create") Map<String, String> params) {

        return authService.getLoggedInUser()
                .map(user -> blogService.postBlog(formBlogFromParams(params, user)))
                .orElse(BlogResult.failure("fail", "log in first"));
    }


    @ApiOperation("Modify existing blog of specific Id")
    @PatchMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult postBlog(@PathVariable("blogId") @ApiParam("Blog Id") Integer blogId,
                               @RequestBody @ApiParam("Blog Information(title, content...) to update") Map<String, String>  params) {

        return authService.getLoggedInUser()
                .map(user -> blogService.updateBlog(blogId, formBlogFromParams(params, user)))
                .orElse(BlogResult.failure("fail", "log in first"));
    }


    @ApiOperation("Delete existing blog of specific Id")
    @DeleteMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult deleteBlog(@PathVariable("blogId") @ApiParam("Blog Id") Integer blogId) {

        return authService.getLoggedInUser()
                .map(user -> blogService.deleteBlog(blogId, user.getId()))
                .orElse(BlogResult.failure("fail", "log in first"));
    }


    @ExceptionHandler({IllegalArgumentException.class})
    public BlogResult handleIllegalArgumentException(IllegalArgumentException e) {
        return BlogResult.failure("fail", e.getMessage());
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
