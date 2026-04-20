package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.SiteConfigDTO;
import com.jay.techportal.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 站点配置公开接口
 */
@Tag(name = "站点配置", description = "站点配置公开查询接口")
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    /**
     * 获取所有公开配置
     */
    @Operation(summary = "获取公开配置", description = "获取所有公开的站点配置")
    @GetMapping
    public Result<List<SiteConfigDTO>> getPublicConfigs() {
        List<SiteConfigDTO> configs = siteConfigService.getPublicConfigs();
        return Result.success(configs);
    }

    /**
     * 获取公开配置的键值对格式
     */
    @Operation(summary = "获取公开配置(键值对)", description = "以键值对形式获取所有公开配置")
    @GetMapping("/map")
    public Result<Map<String, String>> getPublicConfigsMap() {
        Map<String, String> configs = siteConfigService.getPublicConfigsMap();
        return Result.success(configs);
    }

    /**
     * 获取单个公开配置
     */
    @Operation(summary = "获取单个配置", description = "根据配置键获取配置值")
    @GetMapping("/{key}")
    public Result<String> getConfigValue(@PathVariable String key) {
        String value = siteConfigService.getConfigValue(key, null);
        return Result.success(value);
    }
}
