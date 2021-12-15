package com.github.jianqi.jianqiblog.service;

import com.github.jianqi.jianqiblog.dao.BlogDao;
import com.github.jianqi.jianqiblog.entity.Blog;
import com.github.jianqi.jianqiblog.entity.BlogListResult;
import com.github.jianqi.jianqiblog.entity.BlogResult;
import com.github.jianqi.jianqiblog.entity.User;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class BlogService {

    private BlogDao blogDao;

    @Inject
    public BlogService(BlogDao blogDao) {
        this.blogDao = blogDao;
    }

    public BlogListResult getBlogs(Integer page,
                                   Integer pageSize,
                                   Integer userId) {

        try {
            List<Blog> blogs = blogDao.getBlogs(page, pageSize, userId);
            int count = blogDao.count(userId);
            int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            return BlogListResult.success("ok", "acquirement successful", count, page, totalPage, blogs);
        } catch (Exception e) {
            return BlogListResult.failure("fail", "system error");
        }
    }

    public BlogResult getBlogById(Integer blogId) {
        int totalBlogNumber = blogDao.count(null);
        if (blogId < 0 || blogId > totalBlogNumber) {
            return BlogResult.failure("fail", "id cannot be negative or exceeds total blog number");
        } else {
            try {
                Blog blog = blogDao.getBlogById(blogId);
                return BlogResult.success("ok", "acquirement successful", null, null, null, blog);
            } catch (Exception e) {
                return BlogResult.failure("fail", "system error");
            }
        }
    }

    public BlogResult postBlog(String title, String content, String description, User loggedInUser) {
        if (loggedInUser == null) {
            return BlogResult.failure("fail", "please log in first");
        } else {
            Blog blog = blogDao.postBlog(title, content, description, loggedInUser);
            return BlogResult.success("ok", "blog created successfully", null, null, null, blog);
        }
    }
}
