package com.jay.techportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证响应体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "认证响应")
public class AuthResponse {

    @Schema(description = "Access Token")
    private String accessToken;

    @Schema(description = "Refresh Token")
    private String refreshToken;

    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access Token 过期时间（秒）")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "头像")
        private String avatarUrl;

        @Schema(description = "角色")
        private String role;
    }
}
