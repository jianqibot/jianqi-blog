package com.github.jianqi.jianqiblog.entity;

public class BlogResult extends Result<Blog> {
    private Integer total;
    private Integer page;
    private Integer totalPage;

    private BlogResult(String status,
                       String msg,
                       Integer total,
                       Integer page,
                       Integer totalPage,
                       Blog blog) {
        super(status, msg, blog);
        this.total = total;
        this.page = page;
        this.totalPage = totalPage;

    }

    public static BlogResult failure(String status, String msg) {
        return new BlogResult(status, msg, null, null, null, null);
    }

    public static BlogResult success(String status,
                                     String msg,
                                     Integer total,
                                     Integer page,
                                     Integer totalPage,
                                     Blog blog) {
        return new BlogResult(status, msg, total, page, totalPage, blog);
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
