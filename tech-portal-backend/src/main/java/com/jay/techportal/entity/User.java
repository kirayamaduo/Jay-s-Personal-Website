package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString
@Entity // 告诉 JPA 这是一个实体
@Table(name = "users") // 对应数据库的 users 表
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String role; // ADMIN or GUEST

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}