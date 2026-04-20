package com.jay.techportal.dto;

import com.jay.techportal.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目详情 DTO（包含完整内容，用于详情页展示）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目详情DTO")
public class ProjectDetailDTO {

    @Schema(description = "项目ID", example = "1")
    private Long id;

    @Schema(description = "项目标题", example = "Tech Portal")
    private String title;

    @Schema(description = "URL友好标识", example = "tech-portal")
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

    @Schema(description = "Star数量")
    private Integer starsCount;

    @Schema(description = "Fork数量")
    private Integer forksCount;

    @Schema(description = "贡献者数量")
    private Integer contributorsCount;

    @Schema(description = "最后提交时间")
    private LocalDateTime lastCommitAt;

    @Schema(description = "技术栈")
    private List<Map<String, String>> techStack;

    @Schema(description = "功能特性列表")
    private List<String> features;

    @Schema(description = "项目截图")
    private List<String> galleryImages;

    @Schema(description = "是否精选")
    private Boolean isFeatured;

    @Schema(description = "排序顺序")
    private Integer displayOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "标签列表")
    private List<TagDTO> tags;

    /**
     * 从 Entity 转换为 DTO
     */
    public static ProjectDetailDTO fromEntity(Project project) {
        if (project == null) {
            return null;
        }

        ProjectDetailDTO dto = new ProjectDetailDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setSlug(project.getSlug());
        dto.setDescription(project.getDescription());
        dto.setContent(project.getContent());
        dto.setIcon(project.getIcon());
        dto.setGradientClass(project.getGradientClass());
        dto.setGithubUrl(project.getGithubUrl());
        dto.setDemoUrl(project.getDemoUrl());
        dto.setStarsCount(project.getStarsCount());
        dto.setForksCount(project.getForksCount());
        dto.setContributorsCount(project.getContributorsCount());
        dto.setLastCommitAt(project.getLastCommitAt());
        dto.setTechStack(project.getTechStack());
        dto.setFeatures(project.getFeatures());
        dto.setGalleryImages(project.getGalleryImages());
        dto.setIsFeatured(project.getIsFeatured());
        dto.setDisplayOrder(project.getDisplayOrder());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());

        // 标签列表
        if (project.getTags() != null) {
            dto.setTags(project.getTags().stream()
                    .map(TagDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
