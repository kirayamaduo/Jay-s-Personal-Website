package com.jay.techportal.controller.admin;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.TagDTO;
import com.jay.techportal.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理接口（需要认证）
 */
@Tag(name = "管理-标签", description = "标签管理的增删改查接口（需要管理员权限）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTagController {

    private final TagService tagService;

    /**
     * 获取所有标签
     */
    @Operation(summary = "获取所有标签", description = "获取所有标签列表")
    @GetMapping
    public Result<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 获取标签详情
     */
    @Operation(summary = "获取标签详情", description = "根据ID获取标签详情")
    @GetMapping("/{id}")
    public Result<TagDTO> getTagById(
            @Parameter(description = "标签ID", required = true)
            @PathVariable Long id) {
        TagDTO tag = tagService.getTagById(id);
        return Result.success(tag);
    }

    /**
     * 创建标签
     */
    @Operation(summary = "创建标签", description = "创建新的标签")
    @PostMapping
    public Result<TagDTO> createTag(@Valid @RequestBody TagDTO tagDTO) {
        TagDTO createdTag = tagService.createTag(tagDTO);
        return Result.success("标签创建成功", createdTag);
    }

    /**
     * 更新标签
     */
    @Operation(summary = "更新标签", description = "根据ID更新标签信息")
    @PutMapping("/{id}")
    public Result<TagDTO> updateTag(
            @Parameter(description = "标签ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody TagDTO tagDTO) {
        TagDTO updatedTag = tagService.updateTag(id, tagDTO);
        return Result.success("标签更新成功", updatedTag);
    }

    /**
     * 删除标签
     */
    @Operation(summary = "删除标签", description = "根据ID删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(
            @Parameter(description = "标签ID", required = true)
            @PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success("标签删除成功", null);
    }
}
