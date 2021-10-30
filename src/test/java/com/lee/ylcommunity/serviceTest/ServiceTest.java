package com.lee.ylcommunity.serviceTest;

import com.lee.ylcommunity.YlcommunityApplication;
import com.lee.ylcommunity.service.UserService;
import com.lee.ylcommunity.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = YlcommunityApplication.class)
public class ServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testUpdateUserPassword() {
        userService.updatePassword(151, "123", "456");
    }

    @Test
    public void testFilter() {
        String text = "这里可以赌博，可以嫖娼，可以吸毒，可以开票，嘻嘻";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
