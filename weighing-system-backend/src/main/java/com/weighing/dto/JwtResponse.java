package com.weighing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class  JwtResponse {
    private String token;// JWT 字符串
    private String role; // admin 或 operator


    public  JwtResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
}
