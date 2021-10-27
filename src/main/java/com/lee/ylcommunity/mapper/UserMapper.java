package com.lee.ylcommunity.mapper;

import com.lee.ylcommunity.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByName(String username);

    User selectByEmail(String email);

    int updateHeaderUrlById(int userId, String headerUrl);

    int updatePasswordById(int userId, String password);
}