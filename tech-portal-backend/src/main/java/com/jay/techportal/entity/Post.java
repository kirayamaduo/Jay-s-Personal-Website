package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne: 多个文章可以对应一个作者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(length = 500)
    private String excerpt;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "rendered_content", columnDefinition = "LONGTEXT")
    private String renderedContent;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @Column(name = "seo_title", length = 100)
    private String seoTitle;

    @Column(name = "seo_description", length = 200)
    private String seoDescription;

    @Column(name = "is_published")
    private Boolean isPublished = false;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "reading_time_minutes")
    private Integer readingTimeMinutes;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ManyToMany: 文章和标签的多对多关系
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags = new HashSet<>();
}