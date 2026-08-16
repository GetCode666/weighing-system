package com.weighing.config;


//拦截请求，验证 JWT

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   @Autowired
   private JwtUtil jwtUtil;

   @Autowired
   private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {
        // 1. 跳过公开接口（如 /api/auth/login）
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            chain.doFilter(request, response); // 直接放行
            return;
        }
        // 1. 从请求头获取 Authorization  // 2. 尝试提取 Token
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);  // 去掉 "Bearer " 前缀
            // 2. 验证 JWT
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }
        // 5. 继续执行后续过滤器（包括 Controller）
        chain.doFilter(request, response);
    }
}
