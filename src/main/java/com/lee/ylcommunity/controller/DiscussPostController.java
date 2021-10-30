package com.lee.ylcommunity.controller;

import com.lee.ylcommunity.entity.DiscussPost;
import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.service.DiscussPostService;
import com.lee.ylcommunity.service.UserService;
import com.lee.ylcommunity.util.CommunityUtil;
import com.lee.ylcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (null == user) {
            return CommunityUtil.getJSONString(403, "您还没有登录！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(String.valueOf(user.getId()));
        post.setContent(content);
        post.setTitle(title);
        post.setCreateTime(new Date());

        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        User user = userService.findUserById(Integer.parseInt(post.getUserId()));
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}
