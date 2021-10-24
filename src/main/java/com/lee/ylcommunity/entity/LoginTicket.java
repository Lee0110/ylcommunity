package com.lee.ylcommunity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTicket {
    private Integer id;

    private Integer userId;

    private String ticket;

    private Integer status;

    private Date expired;
}