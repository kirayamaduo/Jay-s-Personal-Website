package com.jay.techportal.service;

import com.jay.techportal.common.ResultCode;
import com.jay.techportal.dto.CreateProjectRequest;
import com.jay.techportal.dto.ProjectDetailDTO;
import com.jay.techportal.dto.ProjectListDTO;
import com.jay.techportal.entity.Project;
import com.jay.techportal.entity.Tag;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.ProjectRepository;
import com.jay.techportal.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TagRepository tagRepository;

    // ==================== 公开 API ====================

    /**
     * 获取所有活跃的项目列表
     */
    @Cacheable(value = "projects", key = "'all'")
    public List<ProjectListDTO> getAllActiveProjects() {
        log.debug("Cache miss: loading all active projects from database");
        return projectRepository.findAllActiveWithTags().stream()
                .map(ProjectListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取精选的活跃项目
     */
    @Cacheable(value = "projects", key = "'featured'")
    public List<ProjectListDTO> getFeaturedProjects() {
        log.debug("Cache miss: loading featured projects from database");
        return projectRepository.findFeaturedActiveWithTags().stream()
                .map(ProjectListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据标签ID获取项目列表
     */
    @Cacheable(value = "projects", key = "'tag-' + #tagId")
    public List<ProjectListDTO> getProjectsByTagId(Long tagId) {
        log.debug("Cache miss: loading projects by tag {} from database", tagId);
        return projectRepository.findActiveByTagId(tagId).stream()
                .map(ProjectListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取活跃的项目详情
     */
    @Cacheable(value = "project", key = "#id")
    public ProjectDetailDTO getActiveProjectById(Long id) {
        log.debug("Cache miss: loading project {} from database", id);
        Project project = projectRepository.findActiveByIdWithTags(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "项目不存在"));
        return ProjectDetailDTO.fromEntity(project);
    }

    /**
     * 根据 slug 获取活跃的项目详情
     */
    @Cacheable(value = "project", key = "'slug-' + #slug")
    public ProjectDetailDTO getActiveProjectBySlug(String slug) {
        log.debug("Cache miss: loading project by slug {} from database", slug);
        Project project = projectRepository.findActiveBySlugWithTags(slug)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "项目不存在"));
        return ProjectDetailDTO.fromEntity(project);
    }

    // ==================== 管理后台 API ====================

    /**
     * 根据 ID 获取项目详情（管理后台用，包括非活跃项目）
     */
    public ProjectDetailDTO getProjectById(Long id) {
        Project project = projectRepository.findByIdWithTags(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "项目不存在"));
        return ProjectDetailDTO.fromEntity(project);
    }

    /**
     * 创建项目
     */
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDetailDTO createProject(CreateProjectRequest request) {
        // 生成或验证 slug
        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = generateSlug(request.getTitle());
        }

        // 检查 slug 是否已存在
        if (projectRepository.existsBySlug(slug)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该 slug 已被使用");
        }

        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setSlug(slug);
        project.setDescription(request.getDescription());
        project.setContent(request.getContent());
        project.setIcon(request.getIcon());
        project.setGradientClass(request.getGradientClass());
        project.setGithubUrl(request.getGithubUrl());
        project.setDemoUrl(request.getDemoUrl());
        project.setTechStack(request.getTechStack());
        project.setFeatures(request.getFeatures());
        project.setGalleryImages(request.getGalleryImages());
        project.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        project.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        project.setIsActive(true);

        // 初始化 GitHub 统计
        project.setStarsCount(0);
        project.setForksCount(0);
        project.setContributorsCount(0);

        // 设置标签
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            project.setTags(tags);
        }

        Project savedProject = projectRepository.save(project);
        return ProjectDetailDTO.fromEntity(savedProject);
    }

    /**
     * 更新项目
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "projects", allEntries = true),
            @CacheEvict(value = "project", key = "#id")
    })
    public ProjectDetailDTO updateProject(Long id, CreateProjectRequest request) {
        Project project = projectRepository.findByIdWithTags(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "项目不存在"));

        // 更新 slug（如果改变）
        if (request.getSlug() != null && !request.getSlug().equals(project.getSlug())) {
            if (projectRepository.existsBySlug(request.getSlug())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "该 slug 已被使用");
            }
            project.setSlug(request.getSlug());
        }

        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            project.setContent(request.getContent());
        }
        if (request.getIcon() != null) {
            project.setIcon(request.getIcon());
        }
        if (request.getGradientClass() != null) {
            project.setGradientClass(request.getGradientClass());
        }
        if (request.getGithubUrl() != null) {
            project.setGithubUrl(request.getGithubUrl());
        }
        if (request.getDemoUrl() != null) {
            project.setDemoUrl(request.getDemoUrl());
        }
        if (request.getTechStack() != null) {
            project.setTechStack(request.getTechStack());
        }
        if (request.getFeatures() != null) {
            project.setFeatures(request.getFeatures());
        }
        if (request.getGalleryImages() != null) {
            project.setGalleryImages(request.getGalleryImages());
        }
        if (request.getIsFeatured() != null) {
            project.setIsFeatured(request.getIsFeatured());
        }
        if (request.getDisplayOrder() != null) {
            project.setDisplayOrder(request.getDisplayOrder());
        }

        // 更新标签
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            project.setTags(tags);
        }

        Project updatedProject = projectRepository.save(project);
        return ProjectDetailDTO.fromEntity(updatedProject);
    }

    /**
     * 删除项目（软删除，设置为非活跃）
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "projects", allEntries = true),
            @CacheEvict(value = "project", key = "#id")
    })
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "项目不存在"));
        project.setIsActive(false);
        projectRepository.save(project);
    }

    /**
     * 生成 slug
     */
    private String generateSlug(String title) {
        String baseSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // 如果 slug 已存在，添加数字后缀
        String slug = baseSlug;
        int counter = 1;
        while (projectRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }
}