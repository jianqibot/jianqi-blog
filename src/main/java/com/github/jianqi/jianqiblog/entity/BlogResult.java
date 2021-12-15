package com.github.jianqi.jianqiblog.entity;


public class BlogResult extends Result<Blog> {

    private BlogResult(String status,
                       String msg,
                       Blog blog) {
        super(status, msg, blog);
    }

    public static BlogResult failure(String status, String msg) {
        return new BlogResult(status, msg, null);
    }

    public static BlogResult success(String status,
                                     String msg,
                                     Blog blog) {
        return new BlogResult(status, msg, blog);
    }
}
