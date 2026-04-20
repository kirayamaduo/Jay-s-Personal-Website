package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 文件资源实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "storage_provider", length = 50)
    private String storageProvider = "local";

    @Column(name = "storage_path", length = 500)
    private String storagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
