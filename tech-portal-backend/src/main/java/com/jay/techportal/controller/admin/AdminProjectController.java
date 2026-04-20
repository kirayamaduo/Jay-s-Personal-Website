package com.jay.techportal.controller.admin;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.CreateProjectRequest;
import com.jay.techportal.dto.ProjectDetailDTO;
import com.jay.techportal.dto.ProjectListDTO;
import com.jay.techportal.service.ProjectService;
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
 * 项目管理接口（需要认证）
 */
@Tag(name = "管理-项目", description = "项目管理的增删改查接口（需要管理员权限）")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProjectController {

    private final ProjectService projectService;

    /**
     * 获取所有项目（包括非活跃）
     */
    @Operation(summary = "获取所有项目", description = "获取所有项目列表（包括非活跃的）")
    @GetMapping
    public Result<List<ProjectListDTO>> getAllProjects() {
        // TODO: 实现获取所有项目（包括非活跃）的方法
        List<ProjectListDTO> projects = projectService.getAllActiveProjects();
        return Result.success(projects);
    }

    /**
     * 获取项目详情
     */
    @Operation(summary = "获取项目详情", description = "根据ID获取项目详情（包括非活跃的）")
    @GetMapping("/{id}")
    public Result<ProjectDetailDTO> getProjectById(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id) {
        ProjectDetailDTO project = projectService.getProjectById(id);
        return Result.success(project);
    }

    /**
     * 创建项目
     */
    @Operation(summary = "创建项目", description = "创建新的项目")
    @PostMapping
    public Result<ProjectDetailDTO> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectDetailDTO createdProject = projectService.createProject(request);
        return Result.success("项目创建成功", createdProject);
    }

    /**
     * 更新项目
     */
    @Operation(summary = "更新项目", description = "根据ID更新项目信息")
    @PutMapping("/{id}")
    public Result<ProjectDetailDTO> updateProject(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectDetailDTO updatedProject = projectService.updateProject(id, request);
        return Result.success("项目更新成功", updatedProject);
    }

    /**
     * 删除项目
     */
    @Operation(summary = "删除项目", description = "根据ID删除项目（软删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProject(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success("项目删除成功", null);
    }
}
