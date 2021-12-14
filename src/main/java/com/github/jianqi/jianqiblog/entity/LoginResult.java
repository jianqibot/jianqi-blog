package com.github.jianqi.jianqiblog.entity;

public class LoginResult extends Result<User> {
    private Boolean isLogin;

    private LoginResult(String status, String msg, User data, Boolean isLogin) {
        super(status, msg, data);
        this.isLogin = isLogin;
    }

    public static LoginResult failure(String status, String msg) {
        return new LoginResult(status, msg, null, false);
    }
        public static LoginResult success(String status, String msg, User user, Boolean isLogin) {
        return new LoginResult(status, msg, user, isLogin);
    }

    public Boolean getLogin() {
        return isLogin;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }
}
