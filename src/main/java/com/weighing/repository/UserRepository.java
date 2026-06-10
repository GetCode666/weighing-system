package com.weighing.repository;

import com.weighing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据用户名查找用户（用于登录认证）
     *
     * @param username 用户名
     * @return 用户信息（可能为空）
     */
    Optional<User> findByUsername(String username);
}
