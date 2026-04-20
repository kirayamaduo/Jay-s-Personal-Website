package com.jay.techportal.service;

import com.jay.techportal.common.ResultCode;
import com.jay.techportal.dto.AuthResponse;
import com.jay.techportal.dto.LoginRequest;
import com.jay.techportal.dto.RefreshTokenRequest;
import com.jay.techportal.entity.User;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.UserRepository;
import com.jay.techportal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * 用户登录
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            // 验证用户名密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));

            // 更新最后登录时间
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // 生成 Token
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());

            log.info("User logged in: {}", request.getUsername());

            return buildAuthResponse(accessToken, refreshToken, user);

        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {}", request.getUsername());
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
    }

    /**
     * 刷新 Token
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 验证 Refresh Token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "无效的 Refresh Token");
        }

        // 检查是否是 refresh token
        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "无效的 Token 类型");
        }

        // 获取用户名
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // 获取用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));

        // 生成新的 Access Token
        String newAccessToken = jwtTokenProvider.generateAccessToken(username);

        log.info("Token refreshed for user: {}", username);

        return buildAuthResponse(newAccessToken, refreshToken, user);
    }

    /**
     * 登出（前端清除 token 即可，这里只做日志记录）
     */
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            log.info("User logged out: {}", authentication.getName());
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取当前登录用户信息
     */
    public AuthResponse.UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));

        return buildUserInfo(user);
    }

    /**
     * 构建认证响应
     */
    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000) // 转换为秒
                .user(buildUserInfo(user))
                .build();
    }

    /**
     * 构建用户信息
     */
    private AuthResponse.UserInfo buildUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .build();
    }
}
