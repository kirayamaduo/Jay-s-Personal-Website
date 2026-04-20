package com.jay.techportal.dto;

import com.jay.techportal.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 文件 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "文件DTO")
public class FileDTO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "访问URL")
    private String url;

    @Schema(description = "存储提供商")
    private String storageProvider;

    @Schema(description = "上传者ID")
    private Long uploadedBy;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 从 Entity 转换为 DTO
     */
    public static FileDTO fromEntity(FileEntity file) {
        if (file == null) {
            return null;
        }
        return FileDTO.builder()
                .id(file.getId())
                .filename(file.getFilename())
                .originalName(file.getOriginalName())
                .mimeType(file.getMimeType())
                .size(file.getSize())
                .url(file.getUrl())
                .storageProvider(file.getStorageProvider())
                .uploadedBy(file.getUploadedBy() != null ? file.getUploadedBy().getId() : null)
                .createdAt(file.getCreatedAt())
                .build();
    }

    /**
     * 格式化文件大小
     */
    public String getFormattedSize() {
        if (size == null) return "0 B";
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
}
