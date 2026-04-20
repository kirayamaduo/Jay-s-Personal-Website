package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.ProjectDetailDTO;
import com.jay.techportal.dto.ProjectListDTO;
import com.jay.techportal.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目公开接口
 */
@Tag(name = "项目", description = "项目公开查询接口")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 获取所有项目
     */
    @Operation(summary = "获取所有项目", description = "获取所有活跃的项目列表")
    @GetMapping
    public Result<List<ProjectListDTO>> getAllProjects(
            @Parameter(description = "标签ID筛选")
            @RequestParam(required = false) Long tagId) {

        List<ProjectListDTO> projects;

        if (tagId != null) {
            projects = projectService.getProjectsByTagId(tagId);
        } else {
            projects = projectService.getAllActiveProjects();
        }

        return Result.success(projects);
    }

    /**
     * 获取精选项目（首页使用）
     */
    @Operation(summary = "获取精选项目", description = "返回所有标记为精选的项目")
    @GetMapping("/featured")
    public Result<List<ProjectListDTO>> getFeaturedProjects() {
        List<ProjectListDTO> projects = projectService.getFeaturedProjects();
        return Result.success(projects);
    }

    /**
     * 根据ID获取项目详情
     */
    @Operation(summary = "获取项目详情", description = "根据项目ID获取详细信息")
    @GetMapping("/{id}")
    public Result<ProjectDetailDTO> getProjectById(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id) {
        ProjectDetailDTO project = projectService.getActiveProjectById(id);
        return Result.success(project);
    }

    /**
     * 根据 slug 获取项目详情
     */
    @Operation(summary = "根据slug获取项目", description = "根据项目的URL友好标识获取详细信息")
    @GetMapping("/slug/{slug}")
    public Result<ProjectDetailDTO> getProjectBySlug(
            @Parameter(description = "项目slug", required = true)
            @PathVariable String slug) {
        ProjectDetailDTO project = projectService.getActiveProjectBySlug(slug);
        return Result.success(project);
    }
}