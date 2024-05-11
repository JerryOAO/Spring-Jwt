package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorizeVO {
    /*
        AuthorizeVO类是一个用来返回给前端的类，这个类是用来返回给前端的
     */
    String username;
    String role;
    String token;
    Date expire;
}
