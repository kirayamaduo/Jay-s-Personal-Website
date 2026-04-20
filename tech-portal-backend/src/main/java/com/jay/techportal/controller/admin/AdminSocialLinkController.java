package com.jay.techportal.controller.admin;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.SocialLinkDTO;
import com.jay.techportal.service.SocialLinkService;
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
 * 社交链接管理接口（需要认证）
 */
@Tag(name = "管理-社交链接", description = "社交链接管理的增删改查接口（需要管理员权限）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/social-links")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSocialLinkController {

    private final SocialLinkService socialLinkService;

    /**
     * 获取所有社交链接
     */
    @Operation(summary = "获取所有社交链接", description = "获取所有社交链接列表")
    @GetMapping
    public Result<List<SocialLinkDTO>> getAllSocialLinks() {
        List<SocialLinkDTO> socialLinks = socialLinkService.getAllActiveSocialLinks();
        return Result.success(socialLinks);
    }

    /**
     * 获取社交链接详情
     */
    @Operation(summary = "获取社交链接详情", description = "根据ID获取社交链接详情")
    @GetMapping("/{id}")
    public Result<SocialLinkDTO> getSocialLinkById(
            @Parameter(description = "社交链接ID", required = true)
            @PathVariable Long id) {
        SocialLinkDTO socialLink = socialLinkService.getSocialLinkById(id);
        return Result.success(socialLink);
    }

    /**
     * 创建社交链接
     */
    @Operation(summary = "创建社交链接", description = "创建新的社交链接")
    @PostMapping
    public Result<SocialLinkDTO> createSocialLink(@Valid @RequestBody SocialLinkDTO socialLinkDTO) {
        SocialLinkDTO createdSocialLink = socialLinkService.createSocialLink(socialLinkDTO);
        return Result.success("社交链接创建成功", createdSocialLink);
    }

    /**
     * 更新社交链接
     */
    @Operation(summary = "更新社交链接", description = "根据ID更新社交链接信息")
    @PutMapping("/{id}")
    public Result<SocialLinkDTO> updateSocialLink(
            @Parameter(description = "社交链接ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SocialLinkDTO socialLinkDTO) {
        SocialLinkDTO updatedSocialLink = socialLinkService.updateSocialLink(id, socialLinkDTO);
        return Result.success("社交链接更新成功", updatedSocialLink);
    }

    /**
     * 删除社交链接
     */
    @Operation(summary = "删除社交链接", description = "根据ID删除社交链接")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSocialLink(
            @Parameter(description = "社交链接ID", required = true)
            @PathVariable Long id) {
        socialLinkService.deleteSocialLink(id);
        return Result.success("社交链接删除成功", null);
    }
}
