package com.jay.techportal.service;

import com.jay.techportal.dto.TagDTO;
import com.jay.techportal.entity.Tag;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.common.ResultCode;
import com.jay.techportal.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    /**
     * 获取所有标签
     */
    @Cacheable(value = "tags", key = "'all'")
    public List<TagDTO> getAllTags() {
        log.debug("Cache miss: loading all tags from database");
        return tagRepository.findAll().stream()
                .map(TagDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取标签
     */
    @Cacheable(value = "tags", key = "#id")
    public TagDTO getTagById(Long id) {
        log.debug("Cache miss: loading tag {} from database", id);
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "标签不存在"));
        return TagDTO.fromEntity(tag);
    }

    /**
     * 根据 slug 获取标签
     */
    @Cacheable(value = "tags", key = "'slug-' + #slug")
    public TagDTO getTagBySlug(String slug) {
        log.debug("Cache miss: loading tag by slug {} from database", slug);
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "标签不存在"));
        return TagDTO.fromEntity(tag);
    }

    /**
     * 创建标签
     */
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public TagDTO createTag(TagDTO tagDTO) {
        // 检查名称是否已存在
        if (tagRepository.existsByName(tagDTO.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "标签名称已存在");
        }

        // 如果提供了 slug，检查是否已存在
        if (tagDTO.getSlug() != null && tagRepository.existsBySlug(tagDTO.getSlug())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "标签 slug 已存在");
        }

        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        tag.setSlug(tagDTO.getSlug() != null ? tagDTO.getSlug() : generateSlug(tagDTO.getName()));
        tag.setColor(tagDTO.getColor());
        tag.setDescription(tagDTO.getDescription());

        Tag savedTag = tagRepository.save(tag);
        return TagDTO.fromEntity(savedTag);
    }

    /**
     * 更新标签
     */
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "标签不存在"));

        // 检查名称是否与其他标签冲突
        if (tagDTO.getName() != null && !tagDTO.getName().equals(tag.getName())) {
            if (tagRepository.existsByName(tagDTO.getName())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "标签名称已存在");
            }
            tag.setName(tagDTO.getName());
        }

        // 检查 slug 是否与其他标签冲突
        if (tagDTO.getSlug() != null && !tagDTO.getSlug().equals(tag.getSlug())) {
            if (tagRepository.existsBySlug(tagDTO.getSlug())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "标签 slug 已存在");
            }
            tag.setSlug(tagDTO.getSlug());
        }

        if (tagDTO.getColor() != null) {
            tag.setColor(tagDTO.getColor());
        }
        if (tagDTO.getDescription() != null) {
            tag.setDescription(tagDTO.getDescription());
        }

        Tag updatedTag = tagRepository.save(tag);
        return TagDTO.fromEntity(updatedTag);
    }

    /**
     * 删除标签
     */
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签不存在");
        }
        tagRepository.deleteById(id);
    }

    /**
     * 根据名称生成 slug（简单实现）
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
