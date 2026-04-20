package com.jay.techportal.security;

import com.jay.techportal.entity.User;
import com.jay.techportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Spring Security 用户详情服务实现
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        return buildUserDetails(user);
    }

    /**
     * 构建 Spring Security 的 UserDetails 对象
     */
    private UserDetails buildUserDetails(User user) {
        // 将角色转换为 Spring Security 的权限格式
        String role = user.getRole() != null ? user.getRole() : "GUEST";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                true,  // enabled
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                Collections.singletonList(authority)
        );
    }
}
