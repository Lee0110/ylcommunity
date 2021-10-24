package com.lee.ylcommunity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Integer id;

    private Integer fromId;

    private Integer toId;

    private String conversationId;

    private Integer status;

    private Date createTime;

    private String content;
}