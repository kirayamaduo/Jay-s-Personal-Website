-- 1. 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    avatar_url VARCHAR(255),
    role VARCHAR(20) DEFAULT 'ADMIN' COMMENT 'ADMIN / GUEST',
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 博客文章表
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id BIGINT,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE COMMENT 'URL友好标识',
    excerpt VARCHAR(500),
    content LONGTEXT COMMENT 'Markdown原始内容',
    rendered_content LONGTEXT,
    cover_image VARCHAR(500),
    seo_title VARCHAR(100),
    seo_description VARCHAR(200),
    is_published BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    reading_time_minutes INT,
    published_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_slug (slug),
    INDEX idx_published (is_published, published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 评论表
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    parent_id BIGINT,
    nickname VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    website VARCHAR(255),
    content TEXT NOT NULL,
    reply_to_nickname VARCHAR(50),
    is_approved BOOLEAN DEFAULT TRUE,
    is_spam BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_post_approved (post_id, is_approved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 项目作品表
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE,
    description VARCHAR(500),
    content LONGTEXT,
    icon VARCHAR(50),
    gradient_class VARCHAR(100),
    github_url VARCHAR(255),
    demo_url VARCHAR(255),
    stars_count INT DEFAULT 0,
    forks_count INT DEFAULT 0,
    contributors_count INT DEFAULT 0,
    last_commit_at TIMESTAMP NULL,
    tech_stack JSON COMMENT '存储技术栈数组',
    features JSON COMMENT '存储功能点数组',
    gallery_images JSON COMMENT '存储图片URL数组',
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_slug (slug),
    INDEX idx_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 标签表
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) UNIQUE,
    color VARCHAR(20),
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 文章-标签 关联表
CREATE TABLE post_tags (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 项目-标签 关联表
CREATE TABLE project_tags (
    project_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, tag_id),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 消息表 (Contact Form)
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(200),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    is_replied BOOLEAN DEFAULT FALSE,
    replied_at TIMESTAMP NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. 系统配置表
CREATE TABLE site_configs (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value TEXT,
    value_type VARCHAR(20) DEFAULT 'string',
    description VARCHAR(255),
    is_public BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. 访问日志表
CREATE TABLE visit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path VARCHAR(500) NOT NULL,
    referrer VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    country VARCHAR(50),
    device_type VARCHAR(20),
    visited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_path_time (visited_at, path(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. 经历表
CREATE TABLE experiences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL COMMENT 'work / education',
    title VARCHAR(100) NOT NULL,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE NULL,
    description TEXT,
    achievements JSON,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type_order (type, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. 文件资源表
CREATE TABLE files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    storage_provider VARCHAR(50) DEFAULT 'local',
    storage_path VARCHAR(500),
    uploaded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. API缓存表
CREATE TABLE integration_cache (
    cache_key VARCHAR(100) PRIMARY KEY,
    data JSON NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    ttl_seconds INT DEFAULT 3600
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. 社交链接表
CREATE TABLE social_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform VARCHAR(50) NOT NULL,
    icon VARCHAR(50),
    label VARCHAR(50) NOT NULL,
    url VARCHAR(255) NOT NULL,
    username VARCHAR(100),
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;