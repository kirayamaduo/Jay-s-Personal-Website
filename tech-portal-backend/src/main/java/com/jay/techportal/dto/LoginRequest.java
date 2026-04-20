package com.jay.techportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求体
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
}
