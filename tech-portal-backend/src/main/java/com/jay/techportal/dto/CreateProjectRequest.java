package com.jay.techportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 创建/更新项目请求体
 */
@Data
@Schema(description = "创建/更新项目请求")
public class CreateProjectRequest {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "项目标题", example = "Tech Portal", required = true)
    private String title;

    @Schema(description = "URL友好标识（留空则自动生成）", example = "tech-portal")
    private String slug;

    @Schema(description = "项目简介")
    private String description;

    @Schema(description = "项目详细内容（Markdown）")
    private String content;

    @Schema(description = "图标", example = "code")
    private String icon;

    @Schema(description = "渐变色CSS类", example = "from-blue-500 to-purple-600")
    private String gradientClass;

    @Schema(description = "GitHub链接")
    private String githubUrl;

    @Schema(description = "演示链接")
    private String demoUrl;

    @Schema(description = "技术栈", example = "[{\"name\":\"React\",\"desc\":\"UI框架\"}]")
    private List<Map<String, String>> techStack;

    @Schema(description = "功能特性列表", example = "[\"功能1\", \"功能2\"]")
    private List<String> features;

    @Schema(description = "项目截图URL列表")
    private List<String> galleryImages;

    @Schema(description = "是否精选", example = "false")
    private Boolean isFeatured = false;

    @Schema(description = "排序顺序", example = "0")
    private Integer displayOrder = 0;

    @Schema(description = "标签ID列表")
    private Set<Long> tagIds;
}
