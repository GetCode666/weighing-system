package com.weighing.Service;


import com.weighing.entity.User;
import com.weighing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情加载服务
 * Spring Security 使用此服务从数据库加载用户信息进行认证
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        // 构建 Spring Security 的 UserDetails 对象
        // 参数：用户名、密码、权限列表（角色前加 ROLE_ 前缀)
        /**
         *
         // 多角色场景示例
         List<GrantedAuthority> authorities = user.getRoles().stream()
         .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
         .collect(Collectors.toList());

         // 2. 构建权限列表
         // 假设用户只有一个角色，使用 singletonList 创建单元素不可变列表
         List<GrantedAuthority> authorities = Collections.singletonList(
         new SimpleGrantedAuthority("ROLE_" + user.getRole())
         );

         // 3. 返回 Spring Security 标准的 UserDetails 对象
         return new org.springframework.security.core.userdetails.User(
         user.getUsername(),
         user.getPassword(),
         authorities // 传入权限列表
         );
         */
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
