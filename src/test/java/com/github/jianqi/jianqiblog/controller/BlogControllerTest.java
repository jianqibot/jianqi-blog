package com.github.jianqi.jianqiblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jianqi.jianqiblog.dao.BlogDao;
import com.github.jianqi.jianqiblog.entity.Blog;
import com.github.jianqi.jianqiblog.entity.User;
import com.github.jianqi.jianqiblog.service.AuthService;
import com.github.jianqi.jianqiblog.service.BlogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class BlogControllerTest {
    private MockMvc mvc;
    @Mock
    private AuthService authService;
    @Mock
    private BlogService blogService;
    @Mock
    private BlogDao blogDao;

    @BeforeEach
    void SetUp() {
        blogService = new BlogService(blogDao);
        mvc = MockMvcBuilders.standaloneSetup(new BlogController(blogService, authService)).build();
    }

    @Test
    void getBlogsReturnFailureWhenExceptionThrown() throws Exception {

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", "1");
        requestParams.add("userId", "1");

        Mockito.when(blogDao.getBlogs(1, 10, 1)).thenThrow(new RuntimeException());

        mvc.perform(get("/blog").queryParams(requestParams))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("system error")));
    }

    @Test
    void getBlogsReturnSuccessWhenInGoodCondition() throws Exception {

        List<Blog> blogs = new ArrayList<>();
        blogs.add(new Blog());
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", "1");
        requestParams.add("userId", "1");

        Mockito.when(blogDao.getBlogs(1, 10, 1)).thenReturn(blogs);
        Mockito.when(blogDao.count(1)).thenReturn(1);

        mvc.perform(get("/blog").queryParams(requestParams))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("acquirement successful")));
    }

    @Test
    void getBlogByIdReturnFailureWhenExceptionThrown() throws Exception {

        Mockito.when(blogDao.count(null)).thenReturn(10);
        Mockito.when(blogDao.getBlogById(1)).thenThrow(new RuntimeException());

        mvc.perform(get("/blog/1"))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("system error")));
    }

    @Test
    void getBlogByIdReturnSuccessWhenInGoodCondition() throws Exception {

        Mockito.when(blogDao.count(null)).thenReturn(10);
        Mockito.when(blogDao.getBlogById(1)).thenReturn(new Blog());

        mvc.perform(get("/blog/1"))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("acquirement successful")));
    }

    @Test
    void postBlogReturnLogInFirstWhenNotLoggedIn() throws Exception {

        mvc.perform(post("/blog").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(storeBlogInfoIntoMap())))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString()
                        .contains("\"msg\":\"log in first\"")));
    }

    @Test
    void postBlogReturnSuccessWhenInGoodCondition() throws Exception {

        Mockito.when(authService.getLoggedInUser()).thenReturn(Optional.of(new User()));

        mvc.perform(post("/blog").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(storeBlogInfoIntoMap())))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString()
                        .contains("blog created successfully")));
    }

    @Test
    void updateBlogReturnLogInFirstWhenNotLoggedIn() throws Exception {

        mvc.perform(patch("/blog/1").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(storeBlogInfoIntoMap())))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString()
                        .contains("\"msg\":\"log in first\"")));
    }

    @Test
    void updateBlogReturnSuccessWhenInGoodCondition() throws Exception {

        User user = new User();
        user.setId(1);
        Blog targetBlog = new Blog();
        targetBlog.setUser(user);

        Mockito.when(authService.getLoggedInUser()).thenReturn(Optional.of(user));
        Mockito.when(blogDao.getBlogById(1)).thenReturn(targetBlog);

        mvc.perform(patch("/blog/1").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(storeBlogInfoIntoMap())))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString()
                        .contains("blog modified successfully")));
    }

    @Test
    void deleteBlogReturnLogInFirstWhenNotLoggedIn() throws Exception {

        mvc.perform(delete("/blog/1"))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString()
                        .contains("\"msg\":\"log in first\"")));
    }

    @Test
    void deleteBlogReturnSuccessWhenInGoodCondition() throws Exception {
        User user = new User();
        user.setId(1);
        Blog targetBlog = new Blog();
        targetBlog.setUser(user);

        Mockito.when(authService.getLoggedInUser()).thenReturn(Optional.of(user));
        Mockito.when(blogDao.getBlogById(1)).thenReturn(targetBlog);

        mvc.perform(delete("/blog/1"))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString()
                        .contains("blog deleted successfully")));
    }

    private Map<String, String> storeBlogInfoIntoMap() {

        Map<String, String> blogInfo = new HashMap<>();
        blogInfo.put("title", "randomTitle");
        blogInfo.put("description", "randomDescription");
        blogInfo.put("content", "randomContent");

        return blogInfo;
    }
}
