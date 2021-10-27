package com.lee.ylcommunity.service;


import com.lee.ylcommunity.entity.LoginTicket;
import com.lee.ylcommunity.entity.User;

import java.util.Map;

public interface UserService {

    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updateHeader(int userId, String headerUrl);

    Map<String, Object> updatePassword(int userId, String oldPassword, String password);
}
