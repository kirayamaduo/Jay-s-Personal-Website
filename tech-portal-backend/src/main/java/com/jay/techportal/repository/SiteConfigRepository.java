package com.jay.techportal.repository;

import com.jay.techportal.entity.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 站点配置 Repository
 */
@Repository
public interface SiteConfigRepository extends JpaRepository<SiteConfig, String> {

    /**
     * 根据配置键查找
     */
    Optional<SiteConfig> findByConfigKey(String configKey);

    /**
     * 获取所有公开配置
     */
    List<SiteConfig> findByIsPublicTrue();

    /**
     * 根据值类型查找
     */
    List<SiteConfig> findByValueType(String valueType);
}
