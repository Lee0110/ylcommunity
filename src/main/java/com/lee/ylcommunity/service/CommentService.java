package com.lee.ylcommunity.service;

import com.lee.ylcommunity.entity.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);
}
