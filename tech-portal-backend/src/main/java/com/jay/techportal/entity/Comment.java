package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联的文章
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Post post;

    // 父评论（用于回复功能）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Comment parent;

    // 子评论列表
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Comment> replies = new ArrayList<>();

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String website;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "reply_to_nickname", length = 50)
    private String replyToNickname;

    @Column(name = "is_approved")
    private Boolean isApproved = true;

    @Column(name = "is_spam")
    private Boolean isSpam = false;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
