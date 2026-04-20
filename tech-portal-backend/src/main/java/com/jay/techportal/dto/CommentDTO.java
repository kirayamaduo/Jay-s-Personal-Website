package com.jay.techportal.dto;

import com.jay.techportal.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论DTO")
public class CommentDTO {

    @Schema(description = "评论ID", example = "1")
    private Long id;

    @Schema(description = "文章ID", example = "1")
    private Long postId;

    @Schema(description = "父评论ID（如果是回复）")
    private Long parentId;

    @Schema(description = "昵称", example = "访客")
    private String nickname;

    @Schema(description = "网站")
    private String website;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "回复的用户昵称")
    private String replyToNickname;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "子回复列表")
    private List<CommentDTO> replies;

    /**
     * 从 Entity 转换为 DTO（不包含子回复）
     */
    public static CommentDTO fromEntity(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost() != null ? comment.getPost().getId() : null);
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setNickname(comment.getNickname());
        dto.setWebsite(comment.getWebsite());
        dto.setContent(comment.getContent());
        dto.setReplyToNickname(comment.getReplyToNickname());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setReplies(new ArrayList<>());

        return dto;
    }

    /**
     * 从 Entity 转换为 DTO（包含子回复，用于树形结构）
     */
    public static CommentDTO fromEntityWithReplies(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDTO dto = fromEntity(comment);

        // 递归转换子回复
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                    .filter(reply -> Boolean.TRUE.equals(reply.getIsApproved()) && !Boolean.TRUE.equals(reply.getIsSpam()))
                    .map(CommentDTO::fromEntityWithReplies)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
