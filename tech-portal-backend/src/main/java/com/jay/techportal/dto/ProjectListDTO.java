package com.jay.techportal.dto;

import com.jay.techportal.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目列表 DTO（不包含完整内容，用于列表展示）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目列表DTO")
public class ProjectListDTO {

    @Schema(description = "项目ID", example = "1")
    private Long id;

    @Schema(description = "项目标题", example = "Tech Portal")
    private String title;

    @Schema(description = "URL友好标识", example = "tech-portal")
    private String slug;

    @Schema(description = "项目简介")
    private String description;

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

    @Schema(description = "技术栈")
    private List<Map<String, String>> techStack;

    @Schema(description = "是否精选")
    private Boolean isFeatured;

    @Schema(description = "排序顺序")
    private Integer displayOrder;

    @Schema(description = "标签列表")
    private List<TagDTO> tags;

    /**
     * 从 Entity 转换为 DTO
     */
    public static ProjectListDTO fromEntity(Project project) {
        if (project == null) {
            return null;
        }

        ProjectListDTO dto = new ProjectListDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setSlug(project.getSlug());
        dto.setDescription(project.getDescription());
        dto.setIcon(project.getIcon());
        dto.setGradientClass(project.getGradientClass());
        dto.setGithubUrl(project.getGithubUrl());
        dto.setDemoUrl(project.getDemoUrl());
        dto.setStarsCount(project.getStarsCount());
        dto.setForksCount(project.getForksCount());
        dto.setTechStack(project.getTechStack());
        dto.setIsFeatured(project.getIsFeatured());
        dto.setDisplayOrder(project.getDisplayOrder());

        // 标签列表
        if (project.getTags() != null) {
            dto.setTags(project.getTags().stream()
                    .map(TagDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
