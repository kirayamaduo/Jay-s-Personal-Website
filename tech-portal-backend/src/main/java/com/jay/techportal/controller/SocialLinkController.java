package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.SocialLinkDTO;
import com.jay.techportal.service.SocialLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交链接公开接口（About 页面使用）
 */
@Tag(name = "社交链接", description = "社交链接公开查询接口")
@RestController
@RequestMapping("/api/social-links")
@RequiredArgsConstructor
public class SocialLinkController {

    private final SocialLinkService socialLinkService;

    /**
     * 获取所有活跃的社交链接
     */
    @Operation(summary = "获取所有社交链接", description = "返回所有活跃的社交链接列表，按排序顺序")
    @GetMapping
    public Result<List<SocialLinkDTO>> getAllSocialLinks() {
        List<SocialLinkDTO> socialLinks = socialLinkService.getAllActiveSocialLinks();
        return Result.success(socialLinks);
    }

    /**
     * 根据ID获取社交链接
     */
    @Operation(summary = "获取社交链接详情", description = "根据ID获取社交链接详细信息")
    @GetMapping("/{id}")
    public Result<SocialLinkDTO> getSocialLinkById(
            @Parameter(description = "社交链接ID", required = true)
            @PathVariable Long id) {
        SocialLinkDTO socialLink = socialLinkService.getSocialLinkById(id);
        return Result.success(socialLink);
    }
}
