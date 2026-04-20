package com.jay.techportal.service;

import com.jay.techportal.dto.SocialLinkDTO;
import com.jay.techportal.entity.SocialLink;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.common.ResultCode;
import com.jay.techportal.repository.SocialLinkRepository;
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
public class SocialLinkService {

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    /**
     * 获取所有活跃的社交链接
     */
    @Cacheable(value = "socialLinks", key = "'all'")
    public List<SocialLinkDTO> getAllActiveSocialLinks() {
        log.debug("Cache miss: loading all social links from database");
        return socialLinkRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(SocialLinkDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取社交链接
     */
    @Cacheable(value = "socialLinks", key = "#id")
    public SocialLinkDTO getSocialLinkById(Long id) {
        log.debug("Cache miss: loading social link {} from database", id);
        SocialLink socialLink = socialLinkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "社交链接不存在"));
        return SocialLinkDTO.fromEntity(socialLink);
    }

    /**
     * 创建社交链接
     */
    @Transactional
    @CacheEvict(value = "socialLinks", allEntries = true)
    public SocialLinkDTO createSocialLink(SocialLinkDTO dto) {
        // 检查平台是否已存在
        if (socialLinkRepository.existsByPlatform(dto.getPlatform())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该平台的社交链接已存在");
        }

        SocialLink socialLink = new SocialLink();
        socialLink.setPlatform(dto.getPlatform());
        socialLink.setIcon(dto.getIcon());
        socialLink.setLabel(dto.getLabel());
        socialLink.setUrl(dto.getUrl());
        socialLink.setUsername(dto.getUsername());
        socialLink.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        socialLink.setIsActive(true);

        SocialLink savedSocialLink = socialLinkRepository.save(socialLink);
        return SocialLinkDTO.fromEntity(savedSocialLink);
    }

    /**
     * 更新社交链接
     */
    @Transactional
    @CacheEvict(value = "socialLinks", allEntries = true)
    public SocialLinkDTO updateSocialLink(Long id, SocialLinkDTO dto) {
        SocialLink socialLink = socialLinkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "社交链接不存在"));

        // 如果更改平台，检查是否与其他链接冲突
        if (dto.getPlatform() != null && !dto.getPlatform().equals(socialLink.getPlatform())) {
            if (socialLinkRepository.existsByPlatform(dto.getPlatform())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "该平台的社交链接已存在");
            }
            socialLink.setPlatform(dto.getPlatform());
        }

        if (dto.getIcon() != null) {
            socialLink.setIcon(dto.getIcon());
        }
        if (dto.getLabel() != null) {
            socialLink.setLabel(dto.getLabel());
        }
        if (dto.getUrl() != null) {
            socialLink.setUrl(dto.getUrl());
        }
        if (dto.getUsername() != null) {
            socialLink.setUsername(dto.getUsername());
        }
        if (dto.getDisplayOrder() != null) {
            socialLink.setDisplayOrder(dto.getDisplayOrder());
        }

        SocialLink updatedSocialLink = socialLinkRepository.save(socialLink);
        return SocialLinkDTO.fromEntity(updatedSocialLink);
    }

    /**
     * 删除社交链接
     */
    @Transactional
    @CacheEvict(value = "socialLinks", allEntries = true)
    public void deleteSocialLink(Long id) {
        if (!socialLinkRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "社交链接不存在");
        }
        socialLinkRepository.deleteById(id);
    }
}
