package com.jay.techportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

/**
 * 创建/更新文章请求体
 */
@Data
@Schema(description = "创建/更新文章请求")
public class CreatePostRequest {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "文章标题", example = "Spring Boot 入门指南", required = true)
    private String title;

    @Schema(description = "URL友好标识（留空则自动生成）", example = "spring-boot-getting-started")
    private String slug;

    @Schema(description = "文章摘要")
    private String excerpt;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "Markdown原始内容", required = true)
    private String content;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "SEO标题")
    private String seoTitle;

    @Schema(description = "SEO描述")
    private String seoDescription;

    @Schema(description = "是否精选", example = "false")
    private Boolean isFeatured = false;

    @Schema(description = "是否立即发布", example = "false")
    private Boolean isPublished = false;

    @Schema(description = "标签ID列表")
    private Set<Long> tagIds;
}
