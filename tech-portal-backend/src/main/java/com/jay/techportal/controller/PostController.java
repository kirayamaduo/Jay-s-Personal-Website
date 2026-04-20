package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.PostDetailDTO;
import com.jay.techportal.dto.PostListDTO;
import com.jay.techportal.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客文章公开接口
 */
@Tag(name = "博客文章", description = "博客文章公开查询接口")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 获取所有已发布的文章
     */
    @Operation(summary = "获取文章列表", description = "获取所有已发布的文章列表")
    @GetMapping
    public Result<List<PostListDTO>> getAllPosts(
            @Parameter(description = "标签ID筛选")
            @RequestParam(required = false) Long tagId,
            @Parameter(description = "标签slug筛选")
            @RequestParam(required = false) String tag) {

        List<PostListDTO> posts;

        if (tagId != null) {
            posts = postService.getPostsByTagId(tagId);
        } else if (tag != null && !tag.isBlank()) {
            posts = postService.getPostsByTagSlug(tag);
        } else {
            posts = postService.getAllPublishedPosts();
        }

        return Result.success(posts);
    }

    /**
     * 获取最新文章（首页使用）
     */
    @Operation(summary = "获取最新文章", description = "获取最新发布的文章列表，用于首页展示")
    @GetMapping("/latest")
    public Result<List<PostListDTO>> getLatestPosts(
            @Parameter(description = "返回数量", example = "5")
            @RequestParam(defaultValue = "5") int limit) {
        List<PostListDTO> posts = postService.getLatestPosts(limit);
        return Result.success(posts);
    }

    /**
     * 获取精选文章
     */
    @Operation(summary = "获取精选文章", description = "获取标记为精选的文章列表")
    @GetMapping("/featured")
    public Result<List<PostListDTO>> getFeaturedPosts() {
        List<PostListDTO> posts = postService.getFeaturedPosts();
        return Result.success(posts);
    }

    /**
     * 根据ID获取文章详情
     */
    @Operation(summary = "获取文章详情", description = "根据文章ID获取详细信息")
    @GetMapping("/{id}")
    public Result<PostDetailDTO> getPostById(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long id) {
        PostDetailDTO post = postService.getPublishedPostById(id);
        return Result.success(post);
    }

    /**
     * 根据 slug 获取文章详情
     */
    @Operation(summary = "根据slug获取文章", description = "根据文章的URL友好标识获取详细信息")
    @GetMapping("/slug/{slug}")
    public Result<PostDetailDTO> getPostBySlug(
            @Parameter(description = "文章slug", required = true)
            @PathVariable String slug) {
        PostDetailDTO post = postService.getPublishedPostBySlug(slug);
        return Result.success(post);
    }
}
