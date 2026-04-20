package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.CommentDTO;
import com.jay.techportal.dto.CreateCommentRequest;
import com.jay.techportal.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论相关接口
 */
@Tag(name = "评论管理", description = "评论的增删改查接口")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    // ==================== 公开 API ====================

    /**
     * 获取文章的评论列表
     */
    @Operation(summary = "获取文章评论", description = "获取指定文章的所有评论（树形结构）")
    @GetMapping("/api/posts/{postId}/comments")
    public Result<List<CommentDTO>> getComments(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    /**
     * 获取文章评论数量
     */
    @Operation(summary = "获取评论数量", description = "获取指定文章的评论数量")
    @GetMapping("/api/posts/{postId}/comments/count")
    public Result<Long> getCommentCount(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long postId) {
        long count = commentService.getCommentCount(postId);
        return Result.success(count);
    }

    /**
     * 发表评论
     */
    @Operation(summary = "发表评论", description = "在指定文章下发表评论")
    @PostMapping("/api/posts/{postId}/comments")
    public Result<CommentDTO> createComment(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request,
            HttpServletRequest httpRequest) {
        CommentDTO createdComment = commentService.createComment(postId, request, httpRequest);
        return Result.success("评论发表成功", createdComment);
    }

    // ==================== 管理接口（后续会加权限控制） ====================

    /**
     * 获取待审核的评论
     */
    @Operation(summary = "获取待审核评论", description = "获取所有待审核的评论列表")
    @GetMapping("/api/admin/comments/pending")
    public Result<List<CommentDTO>> getPendingComments() {
        List<CommentDTO> comments = commentService.getPendingComments();
        return Result.success(comments);
    }

    /**
     * 审核评论
     */
    @Operation(summary = "审核评论", description = "将评论标记为已审核")
    @PutMapping("/api/admin/comments/{id}/approve")
    public Result<CommentDTO> approveComment(
            @Parameter(description = "评论ID", required = true)
            @PathVariable Long id) {
        CommentDTO comment = commentService.approveComment(id);
        return Result.success("评论审核通过", comment);
    }

    /**
     * 标记为垃圾评论
     */
    @Operation(summary = "标记垃圾评论", description = "将评论标记为垃圾评论")
    @PutMapping("/api/admin/comments/{id}/spam")
    public Result<Void> markAsSpam(
            @Parameter(description = "评论ID", required = true)
            @PathVariable Long id) {
        commentService.markAsSpam(id);
        return Result.success("已标记为垃圾评论", null);
    }

    /**
     * 删除评论
     */
    @Operation(summary = "删除评论", description = "删除指定评论")
    @DeleteMapping("/api/admin/comments/{id}")
    public Result<Void> deleteComment(
            @Parameter(description = "评论ID", required = true)
            @PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success("评论删除成功", null);
    }
}
