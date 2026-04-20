package com.jay.techportal.repository;

import com.jay.techportal.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    /**
     * 根据类型查找所有活跃的经历，按 displayOrder 排序
     */
    List<Experience> findByTypeAndIsActiveTrueOrderByDisplayOrderAsc(String type);

    /**
     * 查找所有活跃的经历，按 displayOrder 排序
     */
    List<Experience> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 根据类型查找所有经历（包括非活跃的），按 displayOrder 排序
     */
    List<Experience> findByTypeOrderByDisplayOrderAsc(String type);
}
