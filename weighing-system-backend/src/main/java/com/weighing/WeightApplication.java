package com.weighing;

import com.weighing.entity.User;
import com.weighing.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication

public class WeightApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeightApplication.class, args);
    }
    /**
     * 启动时自动创建默认用户（admin / operator）
     * 仅当数据库中不存在该用户名时才会创建
     */
    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("123456"));
                admin.setRole("admin");
                userRepository.save(admin);
                System.out.println("默认管理员用户已创建：admin / 123456");
            }
            if (userRepository.findByUsername("operator").isEmpty()) {
                User operator = new User();
                operator.setUsername("operator");
                operator.setPasswordHash(passwordEncoder.encode("123456"));
                operator.setRole("operator");
                userRepository.save(operator);
                System.out.println("默认操作员用户已创建：operator / 123456");
            }
        };
    }
}
