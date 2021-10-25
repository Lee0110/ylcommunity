package com.lee.ylcommunity.service;


import com.lee.ylcommunity.entity.User;

import java.util.Map;

public interface UserService {

    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);
}
