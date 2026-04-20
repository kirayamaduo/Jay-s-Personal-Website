package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(unique = true, length = 100)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 50)
    private String icon;

    @Column(name = "gradient_class", length = 100)
    private String gradientClass;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "demo_url")
    private String demoUrl;

    // GitHub 统计数据
    @Column(name = "stars_count")
    private Integer starsCount = 0;

    @Column(name = "forks_count")
    private Integer forksCount = 0;

    @Column(name = "contributors_count")
    private Integer contributorsCount = 0;

    @Column(name = "last_commit_at")
    private LocalDateTime lastCommitAt;

    // JSON 字段
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_stack")
    private List<Map<String, String>> techStack; // [{"name":"React", "desc":"UI"}]

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> features; // ["功能1", "功能2"]

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gallery_images")
    private List<String> galleryImages; // ["url1", "url2"]

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // ManyToMany: 项目和标签的多对多关系
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_tags",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags = new HashSet<>();
}