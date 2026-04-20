package com.jay.techportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建消息请求体（联系表单）
 */
@Data
@Schema(description = "联系表单请求")
public class CreateMessageRequest {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名最多100个字符")
    @Schema(description = "姓名", example = "张三", required = true)
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱最多100个字符")
    @Schema(description = "邮箱", example = "zhangsan@example.com", required = true)
    private String email;

    @Size(max = 200, message = "主题最多200个字符")
    @Schema(description = "主题")
    private String subject;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容", required = true)
    private String content;
}
