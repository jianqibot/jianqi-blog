package com.github.jianqi.jianqiblog.service;

import com.github.jianqi.jianqiblog.dao.BlogDao;
import com.github.jianqi.jianqiblog.entity.Blog;
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
        Assertions.assertTrue(blogService.getBlogs(1, 10, null).getMsg().contains("system error"));
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
        Assertions.assertTrue(blogService.getBlogById(1).getMsg().contains("system error"));
    }

    @Test
    void getBlogById() {
        when(blogDao.count(null)).thenReturn(10);
        when(blogDao.getBlogById(1)).thenReturn(Mockito.mock(Blog.class));
        Assertions.assertTrue(blogService.getBlogById(1).getMsg().contains("acquirement successful"));
    }

    @Test
    void postBlogReturnFailureWhenNotLoggedIn() {
        Blog blog = Mockito.mock(Blog.class);
        when(blogDao.postBlog(blog)).thenThrow(new RuntimeException());
        Assertions.assertTrue(blogService.postBlog(blog).getStatus().contains("fail"));
    }

    @Test
    void postBlogReturnSuccessWhenLoggedIn() {
        Blog blog = Mockito.mock(Blog.class);
        when(blogDao.postBlog(blog)).thenReturn(blog);
        Assertions.assertTrue(blogService.postBlog(blog).getMsg().contains("blog created successfully"));
    }

    @Test
    void updateBlogReturnNotFoundErrorWhenBlogDoesNotExist() {
        Blog blog = Mockito.mock(Blog.class);
        Assertions.assertTrue(blogService.updateBlog(1, blog).getMsg().contains("blog does not exist"));
    }

    @Test
    void updateBlogReturnNoAccessWhenDifferentUserLogin() {
        Blog blogFromDB = new Blog();
        User user = new User();
        Blog blogToUpdate = new Blog();
        user.setId(2);
        blogToUpdate.setUserId(1);
        blogFromDB.setUser(user);
        Mockito.when(blogDao.getBlogById(1)).thenReturn(blogFromDB);
        Assertions.assertTrue(blogService.updateBlog(1, blogToUpdate).getMsg().contains("access denied"));
    }

    @Test

    void updateBlogReturnSuccessWhenInGoodCondition() {

        Blog blogFromDB = new Blog();
        User user = new User();
        Blog blogToUpdate = new Blog();
        user.setId(1);
        blogToUpdate.setUserId(1);
        blogFromDB.setUser(user);
        Mockito.when(blogDao.getBlogById(1)).thenReturn(blogFromDB);
        Assertions.assertTrue(blogService.updateBlog(1, blogToUpdate).getMsg().contains("blog modified successfully"));
    }

    @Test
    void deleteBlogReturnNotFoundErrorWhenBlogDoesNotExist() {
        Assertions.assertTrue(blogService.deleteBlog(1, 1).getMsg().contains("blog does not exist"));
    }

    @Test
    void deleteBlogReturnNoAccessWhenDifferentUserLogin() {
        Blog blogFromDB = new Blog();
        User user = new User();
        user.setId(2);
        blogFromDB.setUser(user);
        Mockito.when(blogDao.getBlogById(1)).thenReturn(blogFromDB);
        Assertions.assertTrue(blogService.deleteBlog(1, 1).getMsg().contains("access denied"));
    }

    @Test
    void deleteBlogReturnSuccessWhenGoodCondition() {
        Blog blogFromDB = new Blog();
        User user = new User();
        user.setId(1);
        blogFromDB.setUser(user);
        Mockito.when(blogDao.getBlogById(1)).thenReturn(blogFromDB);
        Assertions.assertTrue(blogService.deleteBlog(1, 1).getMsg().contains("blog deleted successfully"));
    }
}
