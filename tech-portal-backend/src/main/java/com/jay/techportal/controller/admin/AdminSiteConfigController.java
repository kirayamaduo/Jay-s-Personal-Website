package com.jay.techportal.controller.admin;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.SiteConfigDTO;
import com.jay.techportal.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 站点配置管理接口
 */
@Tag(name = "管理-站点配置", description = "站点配置管理接口（需要管理员权限）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSiteConfigController {

    private final SiteConfigService siteConfigService;

    /**
     * 获取所有配置
     */
    @Operation(summary = "获取所有配置", description = "获取所有站点配置（包括非公开的）")
    @GetMapping
    public Result<List<SiteConfigDTO>> getAllConfigs() {
        List<SiteConfigDTO> configs = siteConfigService.getAllConfigs();
        return Result.success(configs);
    }

    /**
     * 获取单个配置
     */
    @Operation(summary = "获取单个配置", description = "根据配置键获取配置详情")
    @GetMapping("/{key}")
    public Result<SiteConfigDTO> getConfig(
            @Parameter(description = "配置键", required = true)
            @PathVariable String key) {
        SiteConfigDTO config = siteConfigService.getConfig(key);
        return Result.success(config);
    }

    /**
     * 创建或更新配置
     */
    @Operation(summary = "保存配置", description = "创建或更新站点配置")
    @PostMapping
    public Result<SiteConfigDTO> saveConfig(@RequestBody SaveConfigRequest request) {
        SiteConfigDTO config = siteConfigService.saveConfig(
                request.getConfigKey(),
                request.getConfigValue(),
                request.getValueType(),
                request.getDescription(),
                request.getIsPublic()
        );
        return Result.success("配置保存成功", config);
    }

    /**
     * 批量更新配置
     */
    @Operation(summary = "批量更新配置", description = "批量更新多个配置项")
    @PutMapping("/batch")
    public Result<Void> batchUpdateConfigs(@RequestBody Map<String, String> configs) {
        siteConfigService.batchUpdateConfigs(configs);
        return Result.success("配置批量更新成功", null);
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置", description = "根据配置键删除配置")
    @DeleteMapping("/{key}")
    public Result<Void> deleteConfig(
            @Parameter(description = "配置键", required = true)
            @PathVariable String key) {
        siteConfigService.deleteConfig(key);
        return Result.success("配置删除成功", null);
    }

    /**
     * 保存配置请求体
     */
    @lombok.Data
    public static class SaveConfigRequest {
        private String configKey;
        private String configValue;
        private String valueType;
        private String description;
        private Boolean isPublic;
    }
}
