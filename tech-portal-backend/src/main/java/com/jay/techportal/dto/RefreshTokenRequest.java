package com.jay.techportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token 请求体
 */
@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "Refresh Token", required = true)
    private String refreshToken;
}
