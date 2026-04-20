package com.jay.techportal.service;

import com.jay.techportal.dto.FileDTO;
import com.jay.techportal.entity.FileEntity;
import com.jay.techportal.entity.User;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.FileRepository;
import com.jay.techportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:/uploads}")
    private String baseUrl;

    @Value("${file.max-size:10485760}") // 10MB default
    private long maxFileSize;

    /**
     * 上传文件
     */
    @Transactional
    public FileDTO uploadFile(MultipartFile file, Long uploaderId) throws IOException {
        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小超过限制");
        }

        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        String filename = generateUniqueFilename(extension);

        // 按日期组织目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = datePath + "/" + filename;

        // 创建目录
        Path uploadPath = Paths.get(uploadDir, datePath);
        Files.createDirectories(uploadPath);

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // 创建数据库记录
        FileEntity fileEntity = FileEntity.builder()
                .filename(filename)
                .originalName(originalName)
                .mimeType(file.getContentType())
                .size(file.getSize())
                .url(baseUrl + "/" + relativePath)
                .storageProvider("local")
                .storagePath(relativePath)
                .build();

        // 设置上传者
        if (uploaderId != null) {
            User uploader = userRepository.findById(uploaderId).orElse(null);
            fileEntity.setUploadedBy(uploader);
        }

        FileEntity saved = fileRepository.save(fileEntity);
        log.info("File uploaded: {} -> {}", originalName, filename);

        return FileDTO.fromEntity(saved);
    }

    /**
     * 获取文件信息
     */
    public FileDTO getFileById(Long id) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文件不存在"));
        return FileDTO.fromEntity(file);
    }

    /**
     * 获取所有文件
     */
    public List<FileDTO> getAllFiles() {
        return fileRepository.findAll().stream()
                .map(FileDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取图片文件
     */
    public List<FileDTO> getImageFiles() {
        return fileRepository.findByMimeTypeStartingWith("image/").stream()
                .map(FileDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 删除文件
     */
    @Transactional
    public void deleteFile(Long id) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 删除物理文件
        if ("local".equals(file.getStorageProvider()) && file.getStoragePath() != null) {
            try {
                Path filePath = Paths.get(uploadDir, file.getStoragePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("Failed to delete physical file: {}", e.getMessage());
            }
        }

        // 删除数据库记录
        fileRepository.delete(file);
        log.info("File deleted: {}", file.getFilename());
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 生成唯一文件名
     */
    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }
}
