package com.lee.ylcommunity.mapperTest;


import com.lee.ylcommunity.YlcommunityApplication;
import com.lee.ylcommunity.entity.DiscussPost;
import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.mapper.DiscussPostMapper;
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
}
