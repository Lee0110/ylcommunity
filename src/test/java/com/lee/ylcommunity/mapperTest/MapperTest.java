package com.lee.ylcommunity.mapperTest;


import com.lee.ylcommunity.YlcommunityApplication;
import com.lee.ylcommunity.entity.DiscussPost;
import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.mapper.DiscussPostMapper;
import com.lee.ylcommunity.mapper.LoginTicketMapper;
import com.lee.ylcommunity.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = YlcommunityApplication.class)
public class MapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPorts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost : discussPorts) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testSelectUser() {
        User user = userMapper.selectByName("aaa");
        System.out.println(user);
    }

    @Test
    public void testInsertTicket() {
//        LoginTicket loginTicket = new LoginTicket();
//        loginTicket.setUserId(101);
//        loginTicket.setTicket("abc");
//        loginTicket.setStatus(1);
//        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

//        LoginTicket loginTicket1 = loginTicketMapper.selectByTicket("abc");
//        System.out.println(loginTicket1);
    }

    @Test
    public void testUpdateUserHeaderUrl() {
        userMapper.updateHeaderUrlById(151, "http://images.nowcoder.com/head/541t.png");
    }

    @Test
    public void testUpdatePassword() {
        userMapper.updatePasswordById(151, "456");
    }
}
