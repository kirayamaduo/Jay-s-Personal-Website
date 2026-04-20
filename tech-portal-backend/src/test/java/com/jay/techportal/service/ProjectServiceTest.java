package com.jay.techportal.service;

import com.jay.techportal.dto.CreateProjectRequest;
import com.jay.techportal.dto.ProjectDetailDTO;
import com.jay.techportal.dto.ProjectListDTO;
import com.jay.techportal.entity.Project;
import com.jay.techportal.entity.Tag;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.ProjectRepository;
import com.jay.techportal.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ProjectService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService 测试")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Tag testTag;
    private CreateProjectRequest createRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试标签
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("React");
        testTag.setSlug("react");
        testTag.setColor("#61dafb");

        // 初始化测试项目
        testProject = new Project();
        testProject.setId(1L);
        testProject.setTitle("Tech Portal");
        testProject.setSlug("tech-portal");
        testProject.setDescription("个人技术博客系统");
        testProject.setContent("这是一个完整的技术博客系统");
        testProject.setIcon("code");
        testProject.setGradientClass("from-blue-500 to-purple-600");
        testProject.setGithubUrl("https://github.com/example/tech-portal");
        testProject.setDemoUrl("https://demo.example.com");
        testProject.setTechStack(Arrays.asList(
                Map.of("name", "Spring Boot", "desc", "后端框架"),
                Map.of("name", "React", "desc", "前端框架"),
                Map.of("name", "MySQL", "desc", "数据库")
        ));
        testProject.setFeatures(Arrays.asList("文章管理", "项目展示", "评论系统"));
        testProject.setIsFeatured(true);
        testProject.setIsActive(true);
        testProject.setDisplayOrder(1);
        testProject.setStarsCount(100);
        testProject.setForksCount(20);
        testProject.setContributorsCount(5);
        testProject.setCreatedAt(LocalDateTime.now());
        testProject.setTags(new HashSet<>(Arrays.asList(testTag)));

        // 初始化创建请求
        createRequest = new CreateProjectRequest();
        createRequest.setTitle("New Project");
        createRequest.setDescription("项目描述");
        createRequest.setContent("项目详细内容");
        createRequest.setIcon("star");
        createRequest.setGradientClass("from-green-500 to-teal-600");
        createRequest.setGithubUrl("https://github.com/example/new-project");
        createRequest.setIsFeatured(false);
        createRequest.setTagIds(new HashSet<>(Arrays.asList(1L)));
    }

    @Nested
    @DisplayName("获取项目列表测试")
    class GetProjectsTests {

        @Test
        @DisplayName("获取所有活跃项目")
        void getAllActiveProjects_Success() {
            // Given
            when(projectRepository.findAllActiveWithTags())
                    .thenReturn(Arrays.asList(testProject));

            // When
            List<ProjectListDTO> result = projectService.getAllActiveProjects();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Tech Portal");
            verify(projectRepository, times(1)).findAllActiveWithTags();
        }

        @Test
        @DisplayName("获取精选项目")
        void getFeaturedProjects_Success() {
            // Given
            when(projectRepository.findFeaturedActiveWithTags())
                    .thenReturn(Arrays.asList(testProject));

            // When
            List<ProjectListDTO> result = projectService.getFeaturedProjects();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIsFeatured()).isTrue();
        }

        @Test
        @DisplayName("根据标签ID获取项目")
        void getProjectsByTagId_Success() {
            // Given
            when(projectRepository.findActiveByTagId(1L))
                    .thenReturn(Arrays.asList(testProject));

            // When
            List<ProjectListDTO> result = projectService.getProjectsByTagId(1L);

            // Then
            assertThat(result).hasSize(1);
            verify(projectRepository, times(1)).findActiveByTagId(1L);
        }
    }

    @Nested
    @DisplayName("获取项目详情测试")
    class GetProjectDetailTests {

        @Test
        @DisplayName("根据ID获取活跃项目详情")
        void getActiveProjectById_Success() {
            // Given
            when(projectRepository.findActiveByIdWithTags(1L))
                    .thenReturn(Optional.of(testProject));

            // When
            ProjectDetailDTO result = projectService.getActiveProjectById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Tech Portal");
        }

        @Test
        @DisplayName("根据ID获取项目 - 项目不存在")
        void getActiveProjectById_NotFound() {
            // Given
            when(projectRepository.findActiveByIdWithTags(999L))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> projectService.getActiveProjectById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("项目不存在");
        }

        @Test
        @DisplayName("根据Slug获取活跃项目详情")
        void getActiveProjectBySlug_Success() {
            // Given
            when(projectRepository.findActiveBySlugWithTags("tech-portal"))
                    .thenReturn(Optional.of(testProject));

            // When
            ProjectDetailDTO result = projectService.getActiveProjectBySlug("tech-portal");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getSlug()).isEqualTo("tech-portal");
        }

        @Test
        @DisplayName("根据Slug获取项目 - 项目不存在")
        void getActiveProjectBySlug_NotFound() {
            // Given
            when(projectRepository.findActiveBySlugWithTags("nonexistent"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> projectService.getActiveProjectBySlug("nonexistent"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("项目不存在");
        }

        @Test
        @DisplayName("管理后台根据ID获取项目详情")
        void getProjectById_Success() {
            // Given
            when(projectRepository.findByIdWithTags(1L))
                    .thenReturn(Optional.of(testProject));

            // When
            ProjectDetailDTO result = projectService.getProjectById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("创建项目测试")
    class CreateProjectTests {

        @Test
        @DisplayName("成功创建项目")
        void createProject_Success() {
            // Given
            Project savedProject = new Project();
            savedProject.setId(2L);
            savedProject.setTitle("New Project");
            savedProject.setSlug("new-project");
            savedProject.setDescription("项目描述");
            savedProject.setIsActive(true);
            savedProject.setIsFeatured(false);
            savedProject.setStarsCount(0);
            savedProject.setForksCount(0);
            savedProject.setContributorsCount(0);
            savedProject.setTags(new HashSet<>(Arrays.asList(testTag)));

            when(projectRepository.existsBySlug(anyString())).thenReturn(false);
            when(tagRepository.findAllById(anySet())).thenReturn(Arrays.asList(testTag));
            when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

            // When
            ProjectDetailDTO result = projectService.createProject(createRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("New Project");
            verify(projectRepository, times(1)).save(any(Project.class));
        }

        @Test
        @DisplayName("创建项目 - 自定义Slug")
        void createProject_WithCustomSlug() {
            // Given
            createRequest.setSlug("custom-project-slug");

            Project savedProject = new Project();
            savedProject.setId(2L);
            savedProject.setTitle("New Project");
            savedProject.setSlug("custom-project-slug");
            savedProject.setIsActive(true);
            savedProject.setTags(new HashSet<>());

            when(projectRepository.existsBySlug("custom-project-slug")).thenReturn(false);
            when(tagRepository.findAllById(anySet())).thenReturn(Arrays.asList(testTag));
            when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

            // When
            ProjectDetailDTO result = projectService.createProject(createRequest);

            // Then
            assertThat(result.getSlug()).isEqualTo("custom-project-slug");
        }

        @Test
        @DisplayName("创建项目 - Slug已存在")
        void createProject_SlugExists() {
            // Given
            createRequest.setSlug("existing-slug");
            when(projectRepository.existsBySlug("existing-slug")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> projectService.createProject(createRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("slug 已被使用");
        }
    }

    @Nested
    @DisplayName("更新项目测试")
    class UpdateProjectTests {

        @Test
        @DisplayName("成功更新项目")
        void updateProject_Success() {
            // Given
            CreateProjectRequest updateRequest = new CreateProjectRequest();
            updateRequest.setTitle("Updated Project");
            updateRequest.setDescription("更新后的描述");

            Project updatedProject = new Project();
            updatedProject.setId(1L);
            updatedProject.setTitle("Updated Project");
            updatedProject.setSlug("tech-portal");
            updatedProject.setDescription("更新后的描述");
            updatedProject.setIsActive(true);
            updatedProject.setTags(new HashSet<>());

            when(projectRepository.findByIdWithTags(1L)).thenReturn(Optional.of(testProject));
            when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

            // When
            ProjectDetailDTO result = projectService.updateProject(1L, updateRequest);

            // Then
            assertThat(result.getTitle()).isEqualTo("Updated Project");
            assertThat(result.getDescription()).isEqualTo("更新后的描述");
        }

        @Test
        @DisplayName("更新项目 - 项目不存在")
        void updateProject_NotFound() {
            // Given
            CreateProjectRequest updateRequest = new CreateProjectRequest();
            updateRequest.setTitle("Updated");

            when(projectRepository.findByIdWithTags(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> projectService.updateProject(999L, updateRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("项目不存在");
        }

        @Test
        @DisplayName("更新项目 - 新Slug已存在")
        void updateProject_SlugConflict() {
            // Given
            CreateProjectRequest updateRequest = new CreateProjectRequest();
            updateRequest.setSlug("existing-slug");

            when(projectRepository.findByIdWithTags(1L)).thenReturn(Optional.of(testProject));
            when(projectRepository.existsBySlug("existing-slug")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> projectService.updateProject(1L, updateRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("slug 已被使用");
        }

        @Test
        @DisplayName("更新项目的标签")
        void updateProject_WithTags() {
            // Given
            Tag newTag = new Tag();
            newTag.setId(2L);
            newTag.setName("Vue");
            newTag.setSlug("vue");

            CreateProjectRequest updateRequest = new CreateProjectRequest();
            updateRequest.setTagIds(new HashSet<>(Arrays.asList(2L)));

            Project updatedProject = new Project();
            updatedProject.setId(1L);
            updatedProject.setTitle("Tech Portal");
            updatedProject.setSlug("tech-portal");
            updatedProject.setIsActive(true);
            updatedProject.setTags(new HashSet<>(Arrays.asList(newTag)));

            when(projectRepository.findByIdWithTags(1L)).thenReturn(Optional.of(testProject));
            when(tagRepository.findAllById(anySet())).thenReturn(Arrays.asList(newTag));
            when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

            // When
            ProjectDetailDTO result = projectService.updateProject(1L, updateRequest);

            // Then
            assertThat(result.getTags()).hasSize(1);
            assertThat(result.getTags().get(0).getName()).isEqualTo("Vue");
        }
    }

    @Nested
    @DisplayName("删除项目测试")
    class DeleteProjectTests {

        @Test
        @DisplayName("成功删除项目（软删除）")
        void deleteProject_Success() {
            // Given
            when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
            when(projectRepository.save(any(Project.class))).thenReturn(testProject);

            // When
            projectService.deleteProject(1L);

            // Then
            verify(projectRepository, times(1)).save(any(Project.class));
            assertThat(testProject.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("删除项目 - 项目不存在")
        void deleteProject_NotFound() {
            // Given
            when(projectRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> projectService.deleteProject(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("项目不存在");
        }
    }
}
