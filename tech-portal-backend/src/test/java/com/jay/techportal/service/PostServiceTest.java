package com.jay.techportal.service;

import com.jay.techportal.dto.CreatePostRequest;
import com.jay.techportal.dto.PostDetailDTO;
import com.jay.techportal.dto.PostListDTO;
import com.jay.techportal.entity.Post;
import com.jay.techportal.entity.Tag;
import com.jay.techportal.entity.User;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.PostRepository;
import com.jay.techportal.repository.TagRepository;
import com.jay.techportal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PostService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 测试")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private User testAuthor;
    private Tag testTag;
    private CreatePostRequest createRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试作者
        testAuthor = new User();
        testAuthor.setId(1L);
        testAuthor.setUsername("admin");
        testAuthor.setEmail("admin@example.com");

        // 初始化测试标签
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("Java");
        testTag.setSlug("java");
        testTag.setColor("#3498db");

        // 初始化测试文章
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Spring Boot 入门指南");
        testPost.setSlug("spring-boot-getting-started");
        testPost.setExcerpt("这是一篇关于Spring Boot的入门文章");
        testPost.setContent("# Spring Boot 入门\n\n这是正文内容...");
        testPost.setIsPublished(true);
        testPost.setIsFeatured(false);
        testPost.setViewCount(100);
        testPost.setReadingTimeMinutes(5);
        testPost.setPublishedAt(LocalDateTime.now());
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setAuthor(testAuthor);
        testPost.setTags(new HashSet<>(Arrays.asList(testTag)));

        // 初始化创建请求
        createRequest = new CreatePostRequest();
        createRequest.setTitle("新文章标题");
        createRequest.setContent("新文章的内容");
        createRequest.setExcerpt("文章摘要");
        createRequest.setIsPublished(true);
        createRequest.setIsFeatured(false);
        createRequest.setTagIds(new HashSet<>(Arrays.asList(1L)));
    }

    @Nested
    @DisplayName("获取已发布文章测试")
    class GetPublishedPostsTests {

        @Test
        @DisplayName("获取所有已发布文章")
        void getAllPublishedPosts_Success() {
            // Given
            when(postRepository.findAllPublishedWithTags())
                    .thenReturn(Arrays.asList(testPost));

            // When
            List<PostListDTO> result = postService.getAllPublishedPosts();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Spring Boot 入门指南");
            verify(postRepository, times(1)).findAllPublishedWithTags();
        }

        @Test
        @DisplayName("获取最新文章")
        void getLatestPosts_Success() {
            // Given
            when(postRepository.findLatestPublished(any(Pageable.class)))
                    .thenReturn(Arrays.asList(testPost));

            // When
            List<PostListDTO> result = postService.getLatestPosts(5);

            // Then
            assertThat(result).hasSize(1);
            verify(postRepository, times(1)).findLatestPublished(any(Pageable.class));
        }

        @Test
        @DisplayName("获取精选文章")
        void getFeaturedPosts_Success() {
            // Given
            testPost.setIsFeatured(true);
            when(postRepository.findFeaturedPublished())
                    .thenReturn(Arrays.asList(testPost));

            // When
            List<PostListDTO> result = postService.getFeaturedPosts();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIsFeatured()).isTrue();
        }

        @Test
        @DisplayName("根据标签ID获取文章")
        void getPostsByTagId_Success() {
            // Given
            when(postRepository.findPublishedByTagId(1L))
                    .thenReturn(Arrays.asList(testPost));

            // When
            List<PostListDTO> result = postService.getPostsByTagId(1L);

            // Then
            assertThat(result).hasSize(1);
            verify(postRepository, times(1)).findPublishedByTagId(1L);
        }

        @Test
        @DisplayName("根据标签Slug获取文章")
        void getPostsByTagSlug_Success() {
            // Given
            when(postRepository.findPublishedByTagSlug("java"))
                    .thenReturn(Arrays.asList(testPost));

            // When
            List<PostListDTO> result = postService.getPostsByTagSlug("java");

            // Then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("获取文章详情测试")
    class GetPostDetailTests {

        @Test
        @DisplayName("根据ID获取已发布文章详情")
        void getPublishedPostById_Success() {
            // Given
            when(postRepository.findPublishedById(1L)).thenReturn(Optional.of(testPost));
            doNothing().when(postRepository).incrementViewCount(1L);

            // When
            PostDetailDTO result = postService.getPublishedPostById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Spring Boot 入门指南");
            assertThat(result.getViewCount()).isEqualTo(101); // 浏览量+1
            verify(postRepository, times(1)).incrementViewCount(1L);
        }

        @Test
        @DisplayName("根据ID获取文章 - 文章不存在")
        void getPublishedPostById_NotFound() {
            // Given
            when(postRepository.findPublishedById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> postService.getPublishedPostById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("文章不存在");
        }

        @Test
        @DisplayName("根据Slug获取已发布文章详情")
        void getPublishedPostBySlug_Success() {
            // Given
            when(postRepository.findPublishedBySlug("spring-boot-getting-started"))
                    .thenReturn(Optional.of(testPost));
            doNothing().when(postRepository).incrementViewCount(1L);

            // When
            PostDetailDTO result = postService.getPublishedPostBySlug("spring-boot-getting-started");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getSlug()).isEqualTo("spring-boot-getting-started");
        }

        @Test
        @DisplayName("根据Slug获取文章 - 文章不存在")
        void getPublishedPostBySlug_NotFound() {
            // Given
            when(postRepository.findPublishedBySlug("nonexistent"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> postService.getPublishedPostBySlug("nonexistent"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("文章不存在");
        }
    }

    @Nested
    @DisplayName("管理后台获取文章测试")
    class AdminGetPostTests {

        @Test
        @DisplayName("获取所有文章（包括草稿）")
        void getAllPosts_Success() {
            // Given
            Post draftPost = new Post();
            draftPost.setId(2L);
            draftPost.setTitle("草稿文章");
            draftPost.setSlug("draft-post");
            draftPost.setIsPublished(false);
            draftPost.setTags(new HashSet<>());

            when(postRepository.findAllWithTags())
                    .thenReturn(Arrays.asList(testPost, draftPost));

            // When
            List<PostListDTO> result = postService.getAllPosts();

            // Then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("根据ID获取文章详情（管理后台）")
        void getPostById_Success() {
            // Given
            when(postRepository.findByIdWithTags(1L)).thenReturn(Optional.of(testPost));

            // When
            PostDetailDTO result = postService.getPostById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("创建文章测试")
    class CreatePostTests {

        @Test
        @DisplayName("成功创建文章")
        void createPost_Success() {
            // Given
            Post savedPost = new Post();
            savedPost.setId(2L);
            savedPost.setTitle("新文章标题");
            savedPost.setSlug("新文章标题");
            savedPost.setContent("新文章的内容");
            savedPost.setIsPublished(true);
            savedPost.setViewCount(0);
            savedPost.setPublishedAt(LocalDateTime.now());
            savedPost.setTags(new HashSet<>(Arrays.asList(testTag)));
            savedPost.setAuthor(testAuthor);

            when(postRepository.existsBySlug(anyString())).thenReturn(false);
            when(tagRepository.findAllById(anySet())).thenReturn(Arrays.asList(testTag));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);

            // When
            PostDetailDTO result = postService.createPost(createRequest, 1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("新文章标题");
            verify(postRepository, times(1)).save(any(Post.class));
        }

        @Test
        @DisplayName("创建文章 - 自定义Slug")
        void createPost_WithCustomSlug() {
            // Given
            createRequest.setSlug("custom-slug");

            Post savedPost = new Post();
            savedPost.setId(2L);
            savedPost.setTitle("新文章标题");
            savedPost.setSlug("custom-slug");
            savedPost.setContent("新文章的内容");
            savedPost.setTags(new HashSet<>());

            when(postRepository.existsBySlug("custom-slug")).thenReturn(false);
            when(tagRepository.findAllById(anySet())).thenReturn(Arrays.asList(testTag));
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);

            // When
            PostDetailDTO result = postService.createPost(createRequest, null);

            // Then
            assertThat(result.getSlug()).isEqualTo("custom-slug");
        }

        @Test
        @DisplayName("创建文章 - Slug已存在")
        void createPost_SlugExists() {
            // Given
            createRequest.setSlug("existing-slug");
            when(postRepository.existsBySlug("existing-slug")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> postService.createPost(createRequest, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("slug 已被使用");
        }

        @Test
        @DisplayName("创建草稿文章")
        void createPost_AsDraft() {
            // Given
            createRequest.setIsPublished(false);

            Post draftPost = new Post();
            draftPost.setId(2L);
            draftPost.setTitle("新文章标题");
            draftPost.setSlug("新文章标题");
            draftPost.setIsPublished(false);
            draftPost.setPublishedAt(null);
            draftPost.setTags(new HashSet<>());

            when(postRepository.existsBySlug(anyString())).thenReturn(false);
            when(tagRepository.findAllById(anySet())).thenReturn(Arrays.asList(testTag));
            when(postRepository.save(any(Post.class))).thenReturn(draftPost);

            // When
            PostDetailDTO result = postService.createPost(createRequest, null);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("新文章标题");
        }
    }

    @Nested
    @DisplayName("更新文章测试")
    class UpdatePostTests {

        @Test
        @DisplayName("成功更新文章")
        void updatePost_Success() {
            // Given
            CreatePostRequest updateRequest = new CreatePostRequest();
            updateRequest.setTitle("更新后的标题");
            updateRequest.setContent("更新后的内容");

            Post updatedPost = new Post();
            updatedPost.setId(1L);
            updatedPost.setTitle("更新后的标题");
            updatedPost.setSlug("spring-boot-getting-started");
            updatedPost.setContent("更新后的内容");
            updatedPost.setTags(new HashSet<>());

            when(postRepository.findByIdWithTags(1L)).thenReturn(Optional.of(testPost));
            when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

            // When
            PostDetailDTO result = postService.updatePost(1L, updateRequest);

            // Then
            assertThat(result.getTitle()).isEqualTo("更新后的标题");
            verify(postRepository, times(1)).save(any(Post.class));
        }

        @Test
        @DisplayName("更新文章 - 文章不存在")
        void updatePost_NotFound() {
            // Given
            CreatePostRequest updateRequest = new CreatePostRequest();
            updateRequest.setTitle("更新标题");

            when(postRepository.findByIdWithTags(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> postService.updatePost(999L, updateRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("文章不存在");
        }

        @Test
        @DisplayName("更新文章 - 新Slug已存在")
        void updatePost_SlugConflict() {
            // Given
            CreatePostRequest updateRequest = new CreatePostRequest();
            updateRequest.setSlug("existing-slug");

            when(postRepository.findByIdWithTags(1L)).thenReturn(Optional.of(testPost));
            when(postRepository.existsBySlug("existing-slug")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> postService.updatePost(1L, updateRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("slug 已被使用");
        }

        @Test
        @DisplayName("更新文章 - 从草稿发布")
        void updatePost_PublishDraft() {
            // Given
            testPost.setIsPublished(false);
            testPost.setPublishedAt(null);

            CreatePostRequest updateRequest = new CreatePostRequest();
            updateRequest.setIsPublished(true);

            Post publishedPost = new Post();
            publishedPost.setId(1L);
            publishedPost.setTitle("Spring Boot 入门指南");
            publishedPost.setSlug("spring-boot-getting-started");
            publishedPost.setIsPublished(true);
            publishedPost.setPublishedAt(LocalDateTime.now());
            publishedPost.setTags(new HashSet<>());

            when(postRepository.findByIdWithTags(1L)).thenReturn(Optional.of(testPost));
            when(postRepository.save(any(Post.class))).thenReturn(publishedPost);

            // When
            PostDetailDTO result = postService.updatePost(1L, updateRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getPublishedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("删除文章测试")
    class DeletePostTests {

        @Test
        @DisplayName("成功删除文章（软删除）")
        void deletePost_Success() {
            // Given
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(postRepository.save(any(Post.class))).thenReturn(testPost);

            // When
            postService.deletePost(1L);

            // Then
            verify(postRepository, times(1)).save(any(Post.class));
            assertThat(testPost.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("删除文章 - 文章不存在")
        void deletePost_NotFound() {
            // Given
            when(postRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> postService.deletePost(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("文章不存在");
        }
    }
}
