package com.github.jianqi.jianqiblog.mapper;

import com.github.jianqi.jianqiblog.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Select("insert into user(username, encrypted_password, avatar, created_at, updated_at) " +
            "values( #{username}, #{encryptedPassword}, 'reserved', now(), now())")
    void saveUserInfo(@Param("username") String username,
                      @Param("encryptedPassword") String encryptedPassword);
}
