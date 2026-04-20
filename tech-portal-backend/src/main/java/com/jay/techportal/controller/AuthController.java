package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.AuthResponse;
import com.jay.techportal.dto.LoginRequest;
import com.jay.techportal.dto.RefreshTokenRequest;
import com.jay.techportal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证相关接口
 */
@Tag(name = "认证管理", description = "用户登录、登出、刷新Token等接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 JWT Token")
    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 刷新 Token
     */
    @Operation(summary = "刷新Token", description = "使用 Refresh Token 获取新的 Access Token")
    @PostMapping("/refresh")
    public Result<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return Result.success("Token 刷新成功", response);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "登出当前用户")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success("登出成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户", description = "获取当前登录用户的信息")
    @GetMapping("/me")
    public Result<AuthResponse.UserInfo> getCurrentUser() {
        AuthResponse.UserInfo user = authService.getCurrentUser();
        return Result.success(user);
    }
}
