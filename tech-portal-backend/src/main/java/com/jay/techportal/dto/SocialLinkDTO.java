package com.jay.techportal.dto;

import com.jay.techportal.entity.SocialLink;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "社交链接DTO")
public class SocialLinkDTO {

    @Schema(description = "链接ID", example = "1")
    private Long id;

    @Schema(description = "平台名称", example = "github")
    private String platform;

    @Schema(description = "图标", example = "github")
    private String icon;

    @Schema(description = "显示标签", example = "GitHub")
    private String label;

    @Schema(description = "链接URL", example = "https://github.com/username")
    private String url;

    @Schema(description = "用户名", example = "username")
    private String username;

    @Schema(description = "排序顺序", example = "0")
    private Integer displayOrder;

    /**
     * 从 Entity 转换为 DTO
     */
    public static SocialLinkDTO fromEntity(SocialLink socialLink) {
        if (socialLink == null) {
            return null;
        }
        return new SocialLinkDTO(
                socialLink.getId(),
                socialLink.getPlatform(),
                socialLink.getIcon(),
                socialLink.getLabel(),
                socialLink.getUrl(),
                socialLink.getUsername(),
                socialLink.getDisplayOrder()
        );
    }
}
