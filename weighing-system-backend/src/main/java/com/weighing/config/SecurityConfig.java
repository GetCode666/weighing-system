package com.weighing.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//配置哪些接口不需要认证

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF (关键：前后端分离必须禁用，否则 POST 请求会被拒)
                .csrf(csrf -> csrf.disable())
                // 2. 设置无状态会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 明确放行登录、注册、H2、Swagger 等公开接口
                        .requestMatchers("/api/auth/login",
                                "/api/auth/register",
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )

                // 4. 配置 Header (可选，用于 H2 控制台)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // 5. 添加 JWT 过滤器
                // 注意：确保 jwtFilter 内部对“无 Token”的情况做了妥善处理（即直接放行，让后续过滤器处理）
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 密码加密器
    }
}
