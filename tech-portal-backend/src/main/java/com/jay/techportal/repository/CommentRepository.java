package com.jay.techportal.repository;

import com.jay.techportal.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 获取文章的顶级评论（已审核、非垃圾，带子回复）
     */
    @Query("SELECT DISTINCT c FROM Comment c " +
            "LEFT JOIN FETCH c.replies r " +
            "WHERE c.post.id = :postId " +
            "AND c.parent IS NULL " +
            "AND c.isApproved = true " +
            "AND c.isSpam = false " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findTopLevelCommentsByPostId(@Param("postId") Long postId);

    /**
     * 获取文章的所有评论（包括回复，已审核、非垃圾）
     */
    @Query("SELECT c FROM Comment c " +
            "WHERE c.post.id = :postId " +
            "AND c.isApproved = true " +
            "AND c.isSpam = false " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findAllApprovedByPostId(@Param("postId") Long postId);

    /**
     * 统计文章的评论数量（已审核、非垃圾）
     */
    @Query("SELECT COUNT(c) FROM Comment c " +
            "WHERE c.post.id = :postId " +
            "AND c.isApproved = true " +
            "AND c.isSpam = false")
    long countApprovedByPostId(@Param("postId") Long postId);

    /**
     * 获取文章的所有评论（管理后台用，包括未审核和垃圾评论）
     */
    @Query("SELECT c FROM Comment c " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findAllByPostId(@Param("postId") Long postId);

    /**
     * 获取待审核的评论
     */
    @Query("SELECT c FROM Comment c " +
            "WHERE c.isApproved = false " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findPendingComments();
}
