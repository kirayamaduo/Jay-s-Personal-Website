package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 站点配置实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "site_configs")
public class SiteConfig {

    @Id
    @Column(name = "config_key", length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "value_type", length = 20)
    private String valueType = "string";

    @Column(length = 255)
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
