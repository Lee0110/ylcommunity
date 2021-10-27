package com.lee.ylcommunity.service.impl;

import com.lee.ylcommunity.entity.LoginTicket;
import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.mapper.LoginTicketMapper;
import com.lee.ylcommunity.mapper.UserMapper;
import com.lee.ylcommunity.service.UserService;
import com.lee.ylcommunity.util.CommunityConstant;
import com.lee.ylcommunity.util.CommunityUtil;
import com.lee.ylcommunity.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        if (null == user) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        User u = userMapper.selectByName(user.getUsername());

        if (null != u) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());

        if (null != u) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insert(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectByPrimaryKey(userId);

        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            user.setStatus(1);
            userMapper.updateByPrimaryKey(user);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        User user = userMapper.selectByName(username);

        if (null == user) {
            map.put("usernameMsg", "账号不存在!");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活,请先前往邮箱激活!");
        }
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insert(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatusByTicket(ticket, 1);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeaderUrlById(userId, headerUrl);
    }

    @Override
    public Map<String, Object> updatePassword(int userId, String oldPassword, String password) {
        Map<String, Object> map = new HashMap<>();

        User user = userMapper.selectByPrimaryKey(userId);
        if (null == user) {
            map.put("error", "用户未登录!");
            return map;
        }
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))) {
            map.put("error", "输入原密码不正确!");
            return map;
        }

        user.setPassword(CommunityUtil.md5(password + user.getSalt()));

        userMapper.updateByPrimaryKey(user);
        return map;
    }
}
