package com.jay.techportal.service;

import com.jay.techportal.common.ResultCode;
import com.jay.techportal.dto.CreatePostRequest;
import com.jay.techportal.dto.PostDetailDTO;
import com.jay.techportal.dto.PostListDTO;
import com.jay.techportal.entity.Post;
import com.jay.techportal.entity.Tag;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.PostRepository;
import com.jay.techportal.repository.TagRepository;
import com.jay.techportal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== 公开 API ====================

    /**
     * 获取所有已发布的文章列表
     */
    @Cacheable(value = "posts", key = "'all'")
    public List<PostListDTO> getAllPublishedPosts() {
        log.debug("Cache miss: loading all published posts from database");
        return postRepository.findAllPublishedWithTags().stream()
                .map(PostListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取最新的已发布文章（首页使用）
     */
    @Cacheable(value = "posts", key = "'latest-' + #limit")
    public List<PostListDTO> getLatestPosts(int limit) {
        log.debug("Cache miss: loading latest {} posts from database", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return postRepository.findLatestPublished(pageable).stream()
                .map(PostListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取精选文章
     */
    @Cacheable(value = "posts", key = "'featured'")
    public List<PostListDTO> getFeaturedPosts() {
        log.debug("Cache miss: loading featured posts from database");
        return postRepository.findFeaturedPublished().stream()
                .map(PostListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据标签ID获取文章列表
     */
    @Cacheable(value = "posts", key = "'tag-' + #tagId")
    public List<PostListDTO> getPostsByTagId(Long tagId) {
        log.debug("Cache miss: loading posts by tag {} from database", tagId);
        return postRepository.findPublishedByTagId(tagId).stream()
                .map(PostListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据标签 slug 获取文章列表
     */
    @Cacheable(value = "posts", key = "'tagSlug-' + #tagSlug")
    public List<PostListDTO> getPostsByTagSlug(String tagSlug) {
        log.debug("Cache miss: loading posts by tag slug {} from database", tagSlug);
        return postRepository.findPublishedByTagSlug(tagSlug).stream()
                .map(PostListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取已发布的文章详情
     */
    @Transactional
    public PostDetailDTO getPublishedPostById(Long id) {
        Post post = postRepository.findPublishedById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "文章不存在"));

        // 增加浏览量
        postRepository.incrementViewCount(id);
        post.setViewCount(post.getViewCount() + 1);

        return PostDetailDTO.fromEntity(post);
    }

    /**
     * 根据 slug 获取已发布的文章详情
     */
    @Transactional
    public PostDetailDTO getPublishedPostBySlug(String slug) {
        Post post = postRepository.findPublishedBySlug(slug)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "文章不存在"));

        // 增加浏览量
        postRepository.incrementViewCount(post.getId());
        post.setViewCount(post.getViewCount() + 1);

        return PostDetailDTO.fromEntity(post);
    }

    // ==================== 管理后台 API ====================

    /**
     * 获取所有文章（管理后台用，包括草稿）
     */
    public List<PostListDTO> getAllPosts() {
        log.debug("Loading all posts (including drafts) from database");
        return postRepository.findAllWithTags().stream()
                .map(PostListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取文章详情（管理后台用，包括草稿）
     */
    public PostDetailDTO getPostById(Long id) {
        Post post = postRepository.findByIdWithTags(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "文章不存在"));
        return PostDetailDTO.fromEntity(post);
    }

    /**
     * 创建文章
     */
    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public PostDetailDTO createPost(CreatePostRequest request, Long authorId) {
        // 生成或验证 slug
        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = generateSlug(request.getTitle());
        }

        // 检查 slug 是否已存在
        if (postRepository.existsBySlug(slug)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该 slug 已被使用");
        }

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setSlug(slug);
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setCoverImage(request.getCoverImage());
        post.setSeoTitle(request.getSeoTitle());
        post.setSeoDescription(request.getSeoDescription());
        post.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        post.setIsPublished(request.getIsPublished() != null ? request.getIsPublished() : false);
        post.setViewCount(0);

        // 计算阅读时间（假设每分钟阅读 200 字）
        if (request.getContent() != null) {
            int wordCount = request.getContent().length();
            post.setReadingTimeMinutes(Math.max(1, wordCount / 200));
        }

        // 如果发布，设置发布时间
        if (Boolean.TRUE.equals(post.getIsPublished())) {
            post.setPublishedAt(LocalDateTime.now());
        }

        // 设置作者
        if (authorId != null) {
            userRepository.findById(authorId).ifPresent(post::setAuthor);
        }

        // 设置标签
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            post.setTags(tags);
        }

        Post savedPost = postRepository.save(post);
        return PostDetailDTO.fromEntity(savedPost);
    }

    /**
     * 更新文章
     */
    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public PostDetailDTO updatePost(Long id, CreatePostRequest request) {
        Post post = postRepository.findByIdWithTags(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "文章不存在"));

        // 更新 slug（如果改变）
        if (request.getSlug() != null && !request.getSlug().equals(post.getSlug())) {
            if (postRepository.existsBySlug(request.getSlug())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "该 slug 已被使用");
            }
            post.setSlug(request.getSlug());
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getExcerpt() != null) {
            post.setExcerpt(request.getExcerpt());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
            // 重新计算阅读时间
            int wordCount = request.getContent().length();
            post.setReadingTimeMinutes(Math.max(1, wordCount / 200));
        }
        if (request.getCoverImage() != null) {
            post.setCoverImage(request.getCoverImage());
        }
        if (request.getSeoTitle() != null) {
            post.setSeoTitle(request.getSeoTitle());
        }
        if (request.getSeoDescription() != null) {
            post.setSeoDescription(request.getSeoDescription());
        }
        if (request.getIsFeatured() != null) {
            post.setIsFeatured(request.getIsFeatured());
        }

        // 处理发布状态变更
        if (request.getIsPublished() != null) {
            boolean wasPublished = Boolean.TRUE.equals(post.getIsPublished());
            boolean willPublish = Boolean.TRUE.equals(request.getIsPublished());

            if (!wasPublished && willPublish) {
                // 从草稿变为发布
                post.setPublishedAt(LocalDateTime.now());
            }
            post.setIsPublished(willPublish);
        }

        // 更新标签
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            post.setTags(tags);
        }

        Post updatedPost = postRepository.save(post);
        return PostDetailDTO.fromEntity(updatedPost);
    }

    /**
     * 删除文章（软删除）
     */
    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "文章不存在"));
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
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
        while (postRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }
}
