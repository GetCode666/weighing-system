package com.weighing.Controller;

//登录接口，生成 JWT

import com.weighing.config.JwtUtil;
import com.weighing.dto.JwtResponse;
import com.weighing.dto.LoginRequest;
import com.weighing.entity.User;
import com.weighing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // 建议统一前缀
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        // 1. 查找用户
        // 注意：为了安全，即使用户不存在，也不要立即抛出特定异常，而是继续执行或统一处理
        User user = userRepository.findByUsername(req.getUsername())
                .orElse(null);

        // 2. 验证凭证
        // 如果用户为空，或者密码不匹配，均视为认证失败
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            // 抛出 Spring Security 标准异常，便于全局异常处理器捕获并返回 401
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 3. 生成 JWT
        // 建议将用户ID也放入Token或响应中，方便后续业务使用
        String token = jwtUtil.generateToken(user.getUsername());

        // 4. 构建响应
        JwtResponse response = new JwtResponse(token, user.getRole());

        return ResponseEntity.ok(response);
    }
}