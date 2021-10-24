package com.lee.ylcommunity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscussPost {
    private Integer id;

    private String userId;

    private String title;

    private Integer type;

    private Integer status;

    private Date createTime;

    private Integer commentCount;

    private Double score;

    private String content;
}