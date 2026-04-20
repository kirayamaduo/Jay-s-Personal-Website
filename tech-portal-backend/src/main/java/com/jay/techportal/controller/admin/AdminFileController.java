package com.jay.techportal.controller.admin;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.FileDTO;
import com.jay.techportal.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件管理接口
 */
@Tag(name = "管理-文件", description = "文件上传和管理接口（需要管理员权限）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFileController {

    private final FileService fileService;

    /**
     * 上传文件
     */
    @Operation(summary = "上传文件", description = "上传单个文件")
    @PostMapping("/upload")
    public Result<FileDTO> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        // TODO: 从 authentication 获取用户ID
        Long uploaderId = null;
        FileDTO fileDTO = fileService.uploadFile(file, uploaderId);
        return Result.success("文件上传成功", fileDTO);
    }

    /**
     * 批量上传文件
     */
    @Operation(summary = "批量上传文件", description = "上传多个文件")
    @PostMapping("/upload/batch")
    public Result<List<FileDTO>> uploadFiles(
            @Parameter(description = "文件列表", required = true)
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication) throws IOException {
        Long uploaderId = null;
        List<FileDTO> fileDTOs = files.stream()
                .map(file -> {
                    try {
                        return fileService.uploadFile(file, uploaderId);
                    } catch (IOException e) {
                        throw new RuntimeException("文件上传失败: " + file.getOriginalFilename(), e);
                    }
                })
                .toList();
        return Result.success("文件上传成功", fileDTOs);
    }

    /**
     * 获取所有文件
     */
    @Operation(summary = "获取所有文件", description = "获取所有已上传的文件列表")
    @GetMapping
    public Result<List<FileDTO>> getAllFiles() {
        List<FileDTO> files = fileService.getAllFiles();
        return Result.success(files);
    }

    /**
     * 获取图片文件
     */
    @Operation(summary = "获取图片文件", description = "获取所有图片类型的文件")
    @GetMapping("/images")
    public Result<List<FileDTO>> getImageFiles() {
        List<FileDTO> files = fileService.getImageFiles();
        return Result.success(files);
    }

    /**
     * 获取文件详情
     */
    @Operation(summary = "获取文件详情", description = "根据ID获取文件详情")
    @GetMapping("/{id}")
    public Result<FileDTO> getFileById(
            @Parameter(description = "文件ID", required = true)
            @PathVariable Long id) {
        FileDTO file = fileService.getFileById(id);
        return Result.success(file);
    }

    /**
     * 删除文件
     */
    @Operation(summary = "删除文件", description = "删除指定文件")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.success("文件删除成功", null);
    }
}
