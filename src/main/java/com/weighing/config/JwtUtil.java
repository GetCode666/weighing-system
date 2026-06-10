package com.weighing.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
//加密  解密  校对
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
//加密
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)//设置主体（通常是用户名）
                .issuedAt(new Date())//签发时间
                .expiration(new Date(System.currentTimeMillis()+expiration))//过期时间
                .signWith(getSigningKey()) // 使用密钥和默认签名算法（HS512）
                .compact()
        ;
    }
    //解密
    // 从 JWT 中提取用户名
    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseEncryptedClaims(token)
                .getPayload()
                .getSubject();
    }
    // 验证 JWT 是否有效（签名正确且未过期）
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
