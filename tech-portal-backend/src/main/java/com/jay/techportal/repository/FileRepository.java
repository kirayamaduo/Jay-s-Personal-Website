package com.jay.techportal.repository;

import com.jay.techportal.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 文件 Repository
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    /**
     * 根据文件名查找
     */
    Optional<FileEntity> findByFilename(String filename);

    /**
     * 根据 MIME 类型查找
     */
    List<FileEntity> findByMimeTypeStartingWith(String mimeTypePrefix);

    /**
     * 根据上传者查找
     */
    List<FileEntity> findByUploadedById(Long userId);

    /**
     * 根据存储提供商查找
     */
    List<FileEntity> findByStorageProvider(String storageProvider);

    /**
     * 检查文件名是否存在
     */
    boolean existsByFilename(String filename);
}
