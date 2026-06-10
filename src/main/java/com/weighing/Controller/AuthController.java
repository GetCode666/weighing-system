package com.weighing.Controller;

import com.weighing.config.JwtUtil;
import com.weighing.dto.JwtResponse;
import com.weighing.dto.LoginRequest;
import com.weighing.entity.User;
import com.weighing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录接口，返回 JWT token
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired  //自动装配。
    //这段代码是 Java Spring 框架中典型的‌依赖注入（Dependency Injection）‌写法。
    //简单来说，它的意思是：‌告诉 Spring 容器，“我需要这三个数据库操作工具（Repository），
    // 请自动帮我创建好并赋值给我，我可以直接使用它们来操作数据库。”
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    /**
     * 用户登录
     * @param req 登录请求（用户名、密码）
     * @return JWT token 和角色
     * @throws RuntimeException 用户不存在或密码错误
     */
    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest req) {
        //根据用户名查找用户
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在: " + req.getUsername()));
        //校验密码
        if(!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())){
            throw new RuntimeException("密码错误");
        }
        String token=jwtUtil.generateToken(user.getUsername());
        return new JwtResponse(token, user.getRole());
    }
}
