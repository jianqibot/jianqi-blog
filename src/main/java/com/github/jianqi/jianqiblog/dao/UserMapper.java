package com.github.jianqi.jianqiblog.dao;

import com.github.jianqi.jianqiblog.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE username = #{username}")
    @Results({
            @Result(property = "encryptedPassword", column = "encrypted_password"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    User findUserByUsername(@Param("username") String username);

    @Select("insert into user(username, encrypted_password, avatar, created_at, updated_at) " +
            "values( #{username}, #{encryptedPassword}, 'reserved', now(), now())")
    void saveUserInfo(@Param("username") String username,
                      @Param("encryptedPassword") String encryptedPassword);
}
