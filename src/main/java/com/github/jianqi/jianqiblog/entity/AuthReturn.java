package com.github.jianqi.jianqiblog.entity;

public class AuthReturn {
    private String status;
    private Boolean isLogin;
    private String msg;
    private User user;

    private AuthReturn(String status, Boolean isLogin, String msg, User user) {
        this.status = status;
        this.isLogin = isLogin;
        this.msg = msg;
        this.user = user;
    }

    public static AuthReturn failureResult (String status, String msg) {
        return new AuthReturn(status, false, msg, null);
    }

    public static AuthReturn successfulResult (String status, String msg, User user) {
        return new AuthReturn(status, true, msg, user);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getLogin() {
        return isLogin;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
