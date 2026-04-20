package com.jay.techportal.dto;

import com.jay.techportal.entity.SiteConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 站点配置 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "站点配置DTO")
public class SiteConfigDTO {

    @Schema(description = "配置键", example = "site_title")
    private String configKey;

    @Schema(description = "配置值", example = "My Tech Portal")
    private String configValue;

    @Schema(description = "值类型", example = "string")
    private String valueType;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 从 Entity 转换为 DTO
     */
    public static SiteConfigDTO fromEntity(SiteConfig config) {
        if (config == null) {
            return null;
        }
        return new SiteConfigDTO(
                config.getConfigKey(),
                config.getConfigValue(),
                config.getValueType(),
                config.getDescription(),
                config.getIsPublic(),
                config.getUpdatedAt()
        );
    }
}
