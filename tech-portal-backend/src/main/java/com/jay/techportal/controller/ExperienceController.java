package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.ExperienceDTO;
import com.jay.techportal.service.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 经历公开接口（About 页面使用）
 */
@Tag(name = "经历", description = "工作/教育经历公开查询接口")
@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    /**
     * 获取所有活跃的经历
     */
    @Operation(summary = "获取所有经历", description = "返回所有活跃的经历列表，按排序顺序")
    @GetMapping
    public Result<List<ExperienceDTO>> getAllExperiences() {
        List<ExperienceDTO> experiences = experienceService.getAllActiveExperiences();
        return Result.success(experiences);
    }

    /**
     * 根据类型获取经历（work/education）
     */
    @Operation(summary = "根据类型获取经历", description = "根据类型（work/education）获取经历列表")
    @GetMapping("/type/{type}")
    public Result<List<ExperienceDTO>> getExperiencesByType(
            @Parameter(description = "经历类型（work/education）", required = true)
            @PathVariable String type) {
        List<ExperienceDTO> experiences = experienceService.getExperiencesByType(type);
        return Result.success(experiences);
    }

    /**
     * 根据ID获取经历详情
     */
    @Operation(summary = "获取经历详情", description = "根据ID获取经历详细信息")
    @GetMapping("/{id}")
    public Result<ExperienceDTO> getExperienceById(
            @Parameter(description = "经历ID", required = true)
            @PathVariable Long id) {
        ExperienceDTO experience = experienceService.getExperienceById(id);
        return Result.success(experience);
    }
}
