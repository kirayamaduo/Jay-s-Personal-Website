package com.jay.techportal.repository;

import com.jay.techportal.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ==================== 公开 API 查询（已发布 + 未删除） ====================

    /**
     * 查询已发布的文章列表（带标签，防止 N+1）
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.isPublished = true AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findAllPublishedWithTags();

    /**
     * 分页查询已发布的文章（不 FETCH，用于分页计数）
     */
    @Query("SELECT p FROM Post p " +
            "WHERE p.isPublished = true AND p.deletedAt IS NULL")
    Page<Post> findAllPublished(Pageable pageable);

    /**
     * 分页查询已发布的文章（带标签，防止 N+1）
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.isPublished = true AND p.deletedAt IS NULL")
    List<Post> findAllPublishedWithTagsAndAuthor();

    /**
     * 根据标签ID查询已发布的文章
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.tags t " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.isPublished = true AND p.deletedAt IS NULL " +
            "AND t.id = :tagId " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findPublishedByTagId(@Param("tagId") Long tagId);

    /**
     * 根据标签 slug 查询已发布的文章
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.tags t " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.isPublished = true AND p.deletedAt IS NULL " +
            "AND t.slug = :tagSlug " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findPublishedByTagSlug(@Param("tagSlug") String tagSlug);

    /**
     * 根据 slug 查询已发布的文章详情
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.slug = :slug AND p.isPublished = true AND p.deletedAt IS NULL")
    Optional<Post> findPublishedBySlug(@Param("slug") String slug);

    /**
     * 根据 ID 查询已发布的文章详情
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.id = :id AND p.isPublished = true AND p.deletedAt IS NULL")
    Optional<Post> findPublishedById(@Param("id") Long id);

    /**
     * 获取最新的已发布文章（首页使用）
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.isPublished = true AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findLatestPublished(Pageable pageable);

    /**
     * 获取精选的已发布文章
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.isPublished = true AND p.isFeatured = true AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findFeaturedPublished();

    // ==================== 管理后台查询（包括草稿） ====================

    /**
     * 根据 slug 查询文章（管理后台用，不限发布状态）
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.slug = :slug AND p.deletedAt IS NULL")
    Optional<Post> findBySlugWithTags(@Param("slug") String slug);

    /**
     * 根据 ID 查询文章（管理后台用，带关联数据）
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Post> findByIdWithTags(@Param("id") Long id);

    /**
     * 检查 slug 是否存在
     */
    boolean existsBySlug(String slug);

    /**
     * 增加浏览量
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 获取所有文章（管理后台用，包括草稿，不包括已删除）
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.tags " +
            "LEFT JOIN FETCH p.author " +
            "WHERE p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllWithTags();

    /**
     * 统计未删除的文章数量
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.deletedAt IS NULL")
    long countActive();
}