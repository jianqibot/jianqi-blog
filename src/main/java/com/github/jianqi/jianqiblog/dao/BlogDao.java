package com.github.jianqi.jianqiblog.dao;

import com.github.jianqi.jianqiblog.entity.Blog;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlogDao {
    private final SqlSession sqlSession;

    @Inject
    public BlogDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<Blog> getBlogs(Integer page,
                               Integer pageSize,
                               Integer userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", userId);
        parameters.put("offset", (page - 1) * pageSize);
        parameters.put("limit", pageSize);
        return sqlSession.selectList("selectBlog", parameters);
    }

    public int count(Integer userId) {
        return sqlSession.selectOne("countBlog", userId);
    }

    public Blog getBlogById(Integer blogId) {
        return sqlSession.selectOne("selectBlogByBlogId", blogId);
    }

    public Blog postBlog(Blog newBlog) {
        sqlSession.insert("insertBlog", newBlog);
        return getBlogById(newBlog.getId());
    }

    public Blog updateBlog(Blog updateBlog) {
        sqlSession.update("updateBlog", updateBlog);
        return getBlogById(updateBlog.getId());

    }

    public void deleteBlog(Integer blogId) {
        sqlSession.delete("deleteBlog", blogId);
    }
}
