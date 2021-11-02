package com.lee.ylcommunity.controller;

import com.lee.ylcommunity.entity.Comment;
import com.lee.ylcommunity.entity.DiscussPost;
import com.lee.ylcommunity.entity.Page;
import com.lee.ylcommunity.entity.User;
import com.lee.ylcommunity.service.CommentService;
import com.lee.ylcommunity.service.DiscussPostService;
import com.lee.ylcommunity.service.UserService;
import com.lee.ylcommunity.util.CommunityConstant;
import com.lee.ylcommunity.util.CommunityUtil;
import com.lee.ylcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        // 作者
        User user = userService.findUserById(Integer.parseInt(post.getUserId()));
        model.addAttribute("user", user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discussPost/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 给帖子的评论
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (null != commentList) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                // 回复
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (null != replyList) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
