package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.TagDTO;
import com.jay.techportal.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签公开接口
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签", description = "标签公开查询接口")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取所有标签
     */
    @Operation(summary = "获取所有标签", description = "返回所有标签列表")
    @GetMapping
    public Result<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 根据ID获取标签
     */
    @Operation(summary = "获取标签详情", description = "根据标签ID获取详细信息")
    @GetMapping("/{id}")
    public Result<TagDTO> getTagById(
            @Parameter(description = "标签ID", required = true)
            @PathVariable Long id) {
        TagDTO tag = tagService.getTagById(id);
        return Result.success(tag);
    }

    /**
     * 根据 slug 获取标签
     */
    @Operation(summary = "根据slug获取标签", description = "根据标签的URL友好标识获取详细信息")
    @GetMapping("/slug/{slug}")
    public Result<TagDTO> getTagBySlug(
            @Parameter(description = "标签slug", required = true)
            @PathVariable String slug) {
        TagDTO tag = tagService.getTagBySlug(slug);
        return Result.success(tag);
    }
}
