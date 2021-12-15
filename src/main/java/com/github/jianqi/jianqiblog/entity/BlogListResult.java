package com.github.jianqi.jianqiblog.entity;

import java.util.List;

public class BlogListResult extends Result<List<Blog>> {
    private Integer total;
    private Integer page;
    private Integer totalPage;

    private BlogListResult(String status,
                           String msg,
                           Integer total,
                           Integer page,
                           Integer totalPage,
                           List<Blog> blogs) {
        super(status, msg, blogs);
        this.total = total;
        this.page = page;
        this.totalPage = totalPage;

    }

    public static BlogListResult failure(String status, String msg) {
        return new BlogListResult(status, msg, null, null, null, null);
    }

    public static BlogListResult success(String status,
                                         String msg,
                                         Integer total,
                                         Integer page,
                                         Integer totalPage,
                                         List<Blog> blogs) {
        return new BlogListResult(status, msg, total, page, totalPage, blogs);
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
