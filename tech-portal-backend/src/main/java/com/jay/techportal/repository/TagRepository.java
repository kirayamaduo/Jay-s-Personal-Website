package com.jay.techportal.repository;

import com.jay.techportal.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 根据名称查找标签
     */
    Optional<Tag> findByName(String name);

    /**
     * 根据 slug 查找标签
     */
    Optional<Tag> findBySlug(String slug);

    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查 slug 是否存在
     */
    boolean existsBySlug(String slug);
}
