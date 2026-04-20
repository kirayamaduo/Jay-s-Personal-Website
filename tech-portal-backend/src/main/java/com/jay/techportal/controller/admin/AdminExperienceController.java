package com.jay.techportal.controller.admin;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.ExperienceDTO;
import com.jay.techportal.service.ExperienceService;
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
 * 经历管理接口（需要认证）
 */
@Tag(name = "管理-经历", description = "工作/教育经历管理的增删改查接口（需要管理员权限）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/experiences")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminExperienceController {

    private final ExperienceService experienceService;

    /**
     * 获取所有经历
     */
    @Operation(summary = "获取所有经历", description = "获取所有经历列表")
    @GetMapping
    public Result<List<ExperienceDTO>> getAllExperiences() {
        List<ExperienceDTO> experiences = experienceService.getAllActiveExperiences();
        return Result.success(experiences);
    }

    /**
     * 获取经历详情
     */
    @Operation(summary = "获取经历详情", description = "根据ID获取经历详情")
    @GetMapping("/{id}")
    public Result<ExperienceDTO> getExperienceById(
            @Parameter(description = "经历ID", required = true)
            @PathVariable Long id) {
        ExperienceDTO experience = experienceService.getExperienceById(id);
        return Result.success(experience);
    }

    /**
     * 创建经历
     */
    @Operation(summary = "创建经历", description = "创建新的经历记录")
    @PostMapping
    public Result<ExperienceDTO> createExperience(@Valid @RequestBody ExperienceDTO experienceDTO) {
        ExperienceDTO createdExperience = experienceService.createExperience(experienceDTO);
        return Result.success("经历创建成功", createdExperience);
    }

    /**
     * 更新经历
     */
    @Operation(summary = "更新经历", description = "根据ID更新经历信息")
    @PutMapping("/{id}")
    public Result<ExperienceDTO> updateExperience(
            @Parameter(description = "经历ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ExperienceDTO experienceDTO) {
        ExperienceDTO updatedExperience = experienceService.updateExperience(id, experienceDTO);
        return Result.success("经历更新成功", updatedExperience);
    }

    /**
     * 删除经历
     */
    @Operation(summary = "删除经历", description = "根据ID删除经历")
    @DeleteMapping("/{id}")
    public Result<Void> deleteExperience(
            @Parameter(description = "经历ID", required = true)
            @PathVariable Long id) {
        experienceService.deleteExperience(id);
        return Result.success("经历删除成功", null);
    }
}
