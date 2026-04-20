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
 * 文章列表 DTO（不包含完整内容，用于列表展示）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章列表DTO")
public class PostListDTO {

    @Schema(description = "文章ID", example = "1")
    private Long id;

    @Schema(description = "文章标题", example = "Spring Boot 入门指南")
    private String title;

    @Schema(description = "URL友好标识", example = "spring-boot-getting-started")
    private String slug;

    @Schema(description = "文章摘要")
    private String excerpt;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "是否精选")
    private Boolean isFeatured;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "阅读时间（分钟）")
    private Integer readingTimeMinutes;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "标签列表")
    private List<TagDTO> tags;

    /**
     * 从 Entity 转换为 DTO
     */
    public static PostListDTO fromEntity(Post post) {
        if (post == null) {
            return null;
        }

        PostListDTO dto = new PostListDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSlug(post.getSlug());
        dto.setExcerpt(post.getExcerpt());
        dto.setCoverImage(post.getCoverImage());
        dto.setIsFeatured(post.getIsFeatured());
        dto.setViewCount(post.getViewCount());
        dto.setReadingTimeMinutes(post.getReadingTimeMinutes());
        dto.setPublishedAt(post.getPublishedAt());

        // 作者名称
        if (post.getAuthor() != null) {
            dto.setAuthorName(post.getAuthor().getUsername());
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
