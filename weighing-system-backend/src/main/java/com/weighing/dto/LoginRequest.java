package com.weighing.dto;


import lombok.Data;

/**
 * 登录请求 DTO
 * 接收前端传来的用户名和密码
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
