package com.jay.techportal.service;

import com.jay.techportal.common.ResultCode;
import com.jay.techportal.dto.CommentDTO;
import com.jay.techportal.dto.CreateCommentRequest;
import com.jay.techportal.entity.Comment;
import com.jay.techportal.entity.Post;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.CommentRepository;
import com.jay.techportal.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    // ==================== 公开 API ====================

    /**
     * 获取文章的评论列表（树形结构）
     */
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        // 验证文章存在
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }

        List<Comment> topLevelComments = commentRepository.findTopLevelCommentsByPostId(postId);

        return topLevelComments.stream()
                .map(CommentDTO::fromEntityWithReplies)
                .collect(Collectors.toList());
    }

    /**
     * 获取文章评论数量
     */
    public long getCommentCount(Long postId) {
        return commentRepository.countApprovedByPostId(postId);
    }

    /**
     * 创建评论
     */
    @Transactional
    public CommentDTO createComment(Long postId, CreateCommentRequest request, HttpServletRequest httpRequest) {
        // 验证文章存在且已发布
        Post post = postRepository.findPublishedById(postId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "文章不存在"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setNickname(request.getNickname());
        comment.setEmail(request.getEmail());
        comment.setWebsite(request.getWebsite());
        comment.setContent(request.getContent());

        // 处理回复
        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "被回复的评论不存在"));

            // 确保父评论属于同一篇文章
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的父评论");
            }

            comment.setParent(parentComment);
            comment.setReplyToNickname(parentComment.getNickname());
        }

        // 记录 IP 和 User-Agent
        if (httpRequest != null) {
            comment.setIpAddress(getClientIp(httpRequest));
            comment.setUserAgent(truncate(httpRequest.getHeader("User-Agent"), 500));
        }

        // 默认自动审核通过（可根据需要修改为待审核）
        comment.setIsApproved(true);
        comment.setIsSpam(false);

        Comment savedComment = commentRepository.save(comment);
        return CommentDTO.fromEntity(savedComment);
    }

    // ==================== 管理后台 API ====================

    /**
     * 审核评论
     */
    @Transactional
    public CommentDTO approveComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "评论不存在"));
        comment.setIsApproved(true);
        Comment savedComment = commentRepository.save(comment);
        return CommentDTO.fromEntity(savedComment);
    }

    /**
     * 标记为垃圾评论
     */
    @Transactional
    public void markAsSpam(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "评论不存在"));
        comment.setIsSpam(true);
        commentRepository.save(comment);
    }

    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评论不存在");
        }
        commentRepository.deleteById(commentId);
    }

    /**
     * 获取待审核的评论列表
     */
    public List<CommentDTO> getPendingComments() {
        return commentRepository.findPendingComments().stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== 工具方法 ====================

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能包含多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 截断字符串
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
