package com.jay.techportal.dto;

import com.jay.techportal.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章详情 DTO（包含完整内容，用于详情页展示）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章详情DTO")
public class PostDetailDTO {

    @Schema(description = "文章ID", example = "1")
    private Long id;

    @Schema(description = "文章标题", example = "Spring Boot 入门指南")
    private String title;

    @Schema(description = "URL友好标识", example = "spring-boot-getting-started")
    private String slug;

    @Schema(description = "文章摘要")
    private String excerpt;

    @Schema(description = "Markdown原始内容")
    private String content;

    @Schema(description = "渲染后的HTML内容")
    private String renderedContent;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "SEO标题")
    private String seoTitle;

    @Schema(description = "SEO描述")
    private String seoDescription;

    @Schema(description = "是否精选")
    private Boolean isFeatured;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "阅读时间（分钟）")
    private Integer readingTimeMinutes;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "作者头像")
    private String authorAvatar;

    @Schema(description = "标签列表")
    private List<TagDTO> tags;

    /**
     * 从 Entity 转换为 DTO
     */
    public static PostDetailDTO fromEntity(Post post) {
        if (post == null) {
            return null;
        }

        PostDetailDTO dto = new PostDetailDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSlug(post.getSlug());
        dto.setExcerpt(post.getExcerpt());
        dto.setContent(post.getContent());
        dto.setRenderedContent(post.getRenderedContent());
        dto.setCoverImage(post.getCoverImage());
        dto.setSeoTitle(post.getSeoTitle());
        dto.setSeoDescription(post.getSeoDescription());
        dto.setIsFeatured(post.getIsFeatured());
        dto.setViewCount(post.getViewCount());
        dto.setReadingTimeMinutes(post.getReadingTimeMinutes());
        dto.setPublishedAt(post.getPublishedAt());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        // 作者信息
        if (post.getAuthor() != null) {
            dto.setAuthorId(post.getAuthor().getId());
            dto.setAuthorName(post.getAuthor().getUsername());
            dto.setAuthorAvatar(post.getAuthor().getAvatarUrl());
        }

        // 标签列表
        if (post.getTags() != null) {
            dto.setTags(post.getTags().stream()
                    .map(TagDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
