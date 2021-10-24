package com.lee.ylcommunity.service.impl;

import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.mapper.UserMapper;
import com.lee.ylcommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
