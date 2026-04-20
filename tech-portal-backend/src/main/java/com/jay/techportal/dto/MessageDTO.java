package com.jay.techportal.dto;

import com.jay.techportal.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息 DTO（管理后台使用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息DTO")
public class MessageDTO {

    @Schema(description = "消息ID", example = "1")
    private Long id;

    @Schema(description = "发送者姓名", example = "张三")
    private String name;

    @Schema(description = "发送者邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "主题")
    private String subject;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "是否已读")
    private Boolean isRead;

    @Schema(description = "是否已回复")
    private Boolean isReplied;

    @Schema(description = "回复时间")
    private LocalDateTime repliedAt;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 从 Entity 转换为 DTO
     */
    public static MessageDTO fromEntity(Message message) {
        if (message == null) {
            return null;
        }
        return new MessageDTO(
                message.getId(),
                message.getName(),
                message.getEmail(),
                message.getSubject(),
                message.getContent(),
                message.getIsRead(),
                message.getIsReplied(),
                message.getRepliedAt(),
                message.getIpAddress(),
                message.getCreatedAt()
        );
    }
}
