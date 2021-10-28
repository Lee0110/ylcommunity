package com.lee.ylcommunity.controller;

import com.lee.ylcommunity.annotation.LoginRequired;
import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.service.UserService;
import com.lee.ylcommunity.util.CommunityUtil;
import com.lee.ylcommunity.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImg, Model model) {
        if (null == headerImg) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        String originalFilename = headerImg.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        originalFilename = CommunityUtil.generateUUID() + suffix;

        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + originalFilename);
        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(Web访问路径)
        User user = hostHolder.getUser();
        if (null == user) {
            model.addAttribute("error", "用户未登录!");
            return "/site/setting";
        }
        String headerUrl = domain + contextPath + "/user/header/" + originalFilename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;

        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        // 响应图片
        response.setContentType("/image" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("读取头像失败:" + e.getMessage());
        }
    }

    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String newPassword2, Model model, @CookieValue("ticket") String ticket) {
        if (!newPassword.equals(newPassword2)) {
            model.addAttribute("error", "两次新密码不一致!");
            return "/site/setting";
        }

        User user = hostHolder.getUser();

        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);

        if (map.containsKey("error")) {
            model.addAttribute("error", map.get("error"));
            return "/site/setting";
        }

        userService.logout(ticket);
        return "redirect:/login";
    }
}
