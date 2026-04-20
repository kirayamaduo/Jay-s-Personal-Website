package com.jay.techportal.repository;

import com.jay.techportal.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {

    /**
     * 查找所有活跃的社交链接，按 displayOrder 排序
     */
    List<SocialLink> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 根据平台查找社交链接
     */
    Optional<SocialLink> findByPlatform(String platform);

    /**
     * 检查平台是否已存在
     */
    boolean existsByPlatform(String platform);
}
