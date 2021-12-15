package com.github.jianqi.jianqiblog.service;

import com.github.jianqi.jianqiblog.dao.BlogDao;
import com.github.jianqi.jianqiblog.entity.Blog;
import com.github.jianqi.jianqiblog.entity.BlogListResult;
import com.github.jianqi.jianqiblog.entity.BlogResult;
import com.github.jianqi.jianqiblog.entity.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {
    @Mock
    private BlogDao blogDao;
    @InjectMocks
    private BlogService blogService;

    @Test
    void getBlogsReturnFailureWhenExceptionThrown() {
        when(blogDao.getBlogs(anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException());
        BlogListResult resultFromGetBlogs = blogService.getBlogs(1, 10, null);
        Assertions.assertEquals("fail", resultFromGetBlogs.getStatus());
        Assertions.assertEquals("system error", resultFromGetBlogs.getMsg());
    }

    @Test
    void getBlogsFromDB() {
        blogService.getBlogs(1, 10, null);
        verify(blogDao).getBlogs(1, 10, null);
    }

    @Test
    void getBlogByIdReturnFailureWhenExceptionThrown() {
        when(blogDao.count(null)).thenReturn(10);
        when(blogDao.getBlogById(anyInt())).thenThrow(new RuntimeException());
        BlogResult resultFromGetBlogByBlogId = blogService.getBlogById(1);
        Assertions.assertEquals("fail", resultFromGetBlogByBlogId.getStatus());
        Assertions.assertEquals("system error", resultFromGetBlogByBlogId.getMsg());
    }

    @Test
    void getBlogById() {
        when(blogDao.count(null)).thenReturn(10);
        when(blogDao.getBlogById(1)).thenReturn(Mockito.mock(Blog.class));
        BlogResult result = blogService.getBlogById(1);
        Assertions.assertTrue(result.getMsg().contains("acquirement successful"));
    }

    @Test
    void postBlogReturnFailureWhenNotLoggedIn() {
        BlogResult result = blogService.postBlog("title", "content", "description", null);
        Assertions.assertTrue(result.getMsg().contains("please log in first"));
    }

    @Test
    void postBlogReturnSuccessWhenLoggedIn() {
        User loggedInUser = Mockito.mock(User.class);
        Blog blog = Mockito.mock(Blog.class);
        when(blogDao.postBlog("title", "content", "description", loggedInUser)).thenReturn(blog);
        BlogResult result = blogService.postBlog("title", "content", "description", loggedInUser);
        Assertions.assertTrue(result.getMsg().contains("blog created successfully"));
    }

}
