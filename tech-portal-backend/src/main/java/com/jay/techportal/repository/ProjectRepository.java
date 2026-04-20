package com.jay.techportal.repository;

import com.jay.techportal.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ==================== 公开 API 查询（活跃项目） ====================

    /**
     * 查询所有活跃项目（带标签，防止 N+1）
     */
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.isActive = true " +
            "ORDER BY p.displayOrder ASC")
    List<Project> findAllActiveWithTags();

    /**
     * 查询精选的活跃项目
     */
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.isActive = true AND p.isFeatured = true " +
            "ORDER BY p.displayOrder ASC")
    List<Project> findFeaturedActiveWithTags();

    /**
     * 根据 ID 查询活跃项目详情
     */
    @Query("SELECT p FROM Project p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.id = :id AND p.isActive = true")
    Optional<Project> findActiveByIdWithTags(@Param("id") Long id);

    /**
     * 根据 slug 查询活跃项目详情
     */
    @Query("SELECT p FROM Project p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.slug = :slug AND p.isActive = true")
    Optional<Project> findActiveBySlugWithTags(@Param("slug") String slug);

    /**
     * 根据标签ID查询活跃项目
     */
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.tags t " +
            "WHERE p.isActive = true AND t.id = :tagId " +
            "ORDER BY p.displayOrder ASC")
    List<Project> findActiveByTagId(@Param("tagId") Long tagId);

    // ==================== 管理后台查询（包括非活跃） ====================

    /**
     * 根据 ID 查询项目（管理后台用，带关联数据）
     */
    @Query("SELECT p FROM Project p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.id = :id")
    Optional<Project> findByIdWithTags(@Param("id") Long id);

    /**
     * 根据 slug 查询项目（管理后台用）
     */
    @Query("SELECT p FROM Project p " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.slug = :slug")
    Optional<Project> findBySlugWithTags(@Param("slug") String slug);

    /**
     * 检查 slug 是否存在
     */
    boolean existsBySlug(String slug);
}