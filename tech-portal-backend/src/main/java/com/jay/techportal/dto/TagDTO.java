package com.jay.techportal.dto;

import com.jay.techportal.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "标签DTO")
public class TagDTO {

    @Schema(description = "标签ID", example = "1")
    private Long id;

    @Schema(description = "标签名称", example = "Java")
    private String name;

    @Schema(description = "URL友好标识", example = "java")
    private String slug;

    @Schema(description = "标签颜色", example = "#3498db")
    private String color;

    @Schema(description = "标签描述", example = "Java相关技术文章")
    private String description;

    /**
     * 从 Entity 转换为 DTO
     */
    public static TagDTO fromEntity(Tag tag) {
        if (tag == null) {
            return null;
        }
        return new TagDTO(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                tag.getColor(),
                tag.getDescription()
        );
    }
}
