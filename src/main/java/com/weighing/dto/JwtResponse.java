package com.weighing.dto;


public class JwtResponse {
    private String token;// JWT 字符串
    private String role; // admin 或 operator


    public JwtResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
}
