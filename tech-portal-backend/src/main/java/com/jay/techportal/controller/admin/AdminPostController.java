package com.jay.techportal.controller.admin;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.CreatePostRequest;
import com.jay.techportal.dto.PostDetailDTO;
import com.jay.techportal.dto.PostListDTO;
import com.jay.techportal.entity.User;
import com.jay.techportal.repository.UserRepository;
import com.jay.techportal.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章管理接口（需要认证）
 */
@Tag(name = "管理-文章", description = "文章管理的增删改查接口（需要管理员权限）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final PostService postService;
    private final UserRepository userRepository;

    /**
     * 获取所有文章（包括草稿）
     */
    @Operation(summary = "获取所有文章", description = "获取所有文章列表（包括未发布的草稿）")
    @GetMapping
    public Result<List<PostListDTO>> getAllPosts() {
        List<PostListDTO> posts = postService.getAllPosts();
        return Result.success(posts);
    }

    /**
     * 获取文章详情（包括草稿）
     */
    @Operation(summary = "获取文章详情", description = "根据ID获取文章详情（包括未发布的草稿）")
    @GetMapping("/{id}")
    public Result<PostDetailDTO> getPostById(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long id) {
        PostDetailDTO post = postService.getPostById(id);
        return Result.success(post);
    }

    /**
     * 创建文章
     */
    @Operation(summary = "创建文章", description = "创建新的博客文章")
    @PostMapping
    public Result<PostDetailDTO> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication) {
        Long authorId = getCurrentUserId(authentication);
        PostDetailDTO createdPost = postService.createPost(request, authorId);
        return Result.success("文章创建成功", createdPost);
    }

    /**
     * 更新文章
     */
    @Operation(summary = "更新文章", description = "根据ID更新文章信息")
    @PutMapping("/{id}")
    public Result<PostDetailDTO> updatePost(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CreatePostRequest request) {
        PostDetailDTO updatedPost = postService.updatePost(id, request);
        return Result.success("文章更新成功", updatedPost);
    }

    /**
     * 删除文章
     */
    @Operation(summary = "删除文章", description = "根据ID删除文章（软删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(
            @Parameter(description = "文章ID", required = true)
            @PathVariable Long id) {
        postService.deletePost(id);
        return Result.success("文章删除成功", null);
    }

    /**
     * 从认证信息获取当前用户ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        // 通过用户名查找用户ID
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElse(null);
    }
}
