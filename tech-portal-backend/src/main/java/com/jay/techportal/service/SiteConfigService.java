package com.jay.techportal.service;

import com.jay.techportal.dto.SiteConfigDTO;
import com.jay.techportal.entity.SiteConfig;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.SiteConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 站点配置 Service
 */
@Service
@RequiredArgsConstructor
public class SiteConfigService {

    private final SiteConfigRepository siteConfigRepository;

    /**
     * 获取所有公开配置
     */
    public List<SiteConfigDTO> getPublicConfigs() {
        return siteConfigRepository.findByIsPublicTrue().stream()
                .map(SiteConfigDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有配置（管理员用）
     */
    public List<SiteConfigDTO> getAllConfigs() {
        return siteConfigRepository.findAll().stream()
                .map(SiteConfigDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取单个配置
     */
    public SiteConfigDTO getConfig(String key) {
        SiteConfig config = siteConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new BusinessException("配置项不存在: " + key));
        return SiteConfigDTO.fromEntity(config);
    }

    /**
     * 获取配置值
     */
    public String getConfigValue(String key, String defaultValue) {
        return siteConfigRepository.findByConfigKey(key)
                .map(SiteConfig::getConfigValue)
                .orElse(defaultValue);
    }

    /**
     * 获取公开配置的键值对
     */
    public Map<String, String> getPublicConfigsMap() {
        return siteConfigRepository.findByIsPublicTrue().stream()
                .collect(Collectors.toMap(
                        SiteConfig::getConfigKey,
                        config -> config.getConfigValue() != null ? config.getConfigValue() : ""
                ));
    }

    /**
     * 创建或更新配置
     */
    @Transactional
    public SiteConfigDTO saveConfig(String key, String value, String valueType, String description, Boolean isPublic) {
        SiteConfig config = siteConfigRepository.findByConfigKey(key)
                .orElse(new SiteConfig());

        config.setConfigKey(key);
        config.setConfigValue(value);
        if (valueType != null) {
            config.setValueType(valueType);
        }
        if (description != null) {
            config.setDescription(description);
        }
        if (isPublic != null) {
            config.setIsPublic(isPublic);
        }

        SiteConfig saved = siteConfigRepository.save(config);
        return SiteConfigDTO.fromEntity(saved);
    }

    /**
     * 批量更新配置
     */
    @Transactional
    public void batchUpdateConfigs(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            SiteConfig config = siteConfigRepository.findByConfigKey(entry.getKey())
                    .orElse(new SiteConfig());
            config.setConfigKey(entry.getKey());
            config.setConfigValue(entry.getValue());
            siteConfigRepository.save(config);
        }
    }

    /**
     * 删除配置
     */
    @Transactional
    public void deleteConfig(String key) {
        if (!siteConfigRepository.existsById(key)) {
            throw new BusinessException("配置项不存在: " + key);
        }
        siteConfigRepository.deleteById(key);
    }
}
