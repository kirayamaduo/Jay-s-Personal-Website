package com.jay.techportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建评论请求体
 */
@Data
@Schema(description = "创建评论请求")
public class CreateCommentRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称最多50个字符")
    @Schema(description = "昵称", example = "访客", required = true)
    private String nickname;

    @Size(max = 100, message = "邮箱最多100个字符")
    @Schema(description = "邮箱（可选，不会公开显示）")
    private String email;

    @Size(max = 255, message = "网站最多255个字符")
    @Schema(description = "网站（可选）")
    private String website;

    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", required = true)
    private String content;

    @Schema(description = "父评论ID（如果是回复某条评论）")
    private Long parentId;
}
