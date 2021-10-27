package com.lee.ylcommunity.controller;

import com.lee.ylcommunity.entity.DiscussPost;
import com.lee.ylcommunity.entity.Page;
import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.service.DiscussPostService;
import com.lee.ylcommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPostMapList = new ArrayList<>();

        if (null != discussPostList) {
            for (DiscussPost discussPost : discussPostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                User user = userService.findUserById(Integer.parseInt(discussPost.getUserId()));
                map.put("user", user);
                discussPostMapList.add(map);
            }
        }

        model.addAttribute("discussPostMapList", discussPostMapList);

        return "/index";
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getIndexPage2(Model model, Page page) {
        return getIndexPage(model, page);
    }
}
