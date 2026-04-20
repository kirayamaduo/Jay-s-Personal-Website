package com.jay.techportal.service;

import com.jay.techportal.dto.ExperienceDTO;
import com.jay.techportal.entity.Experience;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.common.ResultCode;
import com.jay.techportal.repository.ExperienceRepository;
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
public class ExperienceService {

    @Autowired
    private ExperienceRepository experienceRepository;

    /**
     * 获取所有活跃的经历
     */
    @Cacheable(value = "experiences", key = "'all'")
    public List<ExperienceDTO> getAllActiveExperiences() {
        log.debug("Cache miss: loading all experiences from database");
        return experienceRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(ExperienceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型获取活跃的经历（work/education）
     */
    @Cacheable(value = "experiences", key = "'type-' + #type")
    public List<ExperienceDTO> getExperiencesByType(String type) {
        log.debug("Cache miss: loading experiences by type {} from database", type);
        return experienceRepository.findByTypeAndIsActiveTrueOrderByDisplayOrderAsc(type).stream()
                .map(ExperienceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取经历
     */
    @Cacheable(value = "experiences", key = "#id")
    public ExperienceDTO getExperienceById(Long id) {
        log.debug("Cache miss: loading experience {} from database", id);
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "经历不存在"));
        return ExperienceDTO.fromEntity(experience);
    }

    /**
     * 创建经历
     */
    @Transactional
    @CacheEvict(value = "experiences", allEntries = true)
    public ExperienceDTO createExperience(ExperienceDTO dto) {
        Experience experience = new Experience();
        experience.setType(dto.getType());
        experience.setTitle(dto.getTitle());
        experience.setCompany(dto.getCompany());
        experience.setLocation(dto.getLocation());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setDescription(dto.getDescription());
        experience.setAchievements(dto.getAchievements());
        experience.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        experience.setIsActive(true);

        Experience savedExperience = experienceRepository.save(experience);
        return ExperienceDTO.fromEntity(savedExperience);
    }

    /**
     * 更新经历
     */
    @Transactional
    @CacheEvict(value = "experiences", allEntries = true)
    public ExperienceDTO updateExperience(Long id, ExperienceDTO dto) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "经历不存在"));

        if (dto.getType() != null) {
            experience.setType(dto.getType());
        }
        if (dto.getTitle() != null) {
            experience.setTitle(dto.getTitle());
        }
        if (dto.getCompany() != null) {
            experience.setCompany(dto.getCompany());
        }
        if (dto.getLocation() != null) {
            experience.setLocation(dto.getLocation());
        }
        if (dto.getStartDate() != null) {
            experience.setStartDate(dto.getStartDate());
        }
        // endDate 可以设置为 null，表示"至今"
        experience.setEndDate(dto.getEndDate());
        if (dto.getDescription() != null) {
            experience.setDescription(dto.getDescription());
        }
        if (dto.getAchievements() != null) {
            experience.setAchievements(dto.getAchievements());
        }
        if (dto.getDisplayOrder() != null) {
            experience.setDisplayOrder(dto.getDisplayOrder());
        }

        Experience updatedExperience = experienceRepository.save(experience);
        return ExperienceDTO.fromEntity(updatedExperience);
    }

    /**
     * 删除经历
     */
    @Transactional
    @CacheEvict(value = "experiences", allEntries = true)
    public void deleteExperience(Long id) {
        if (!experienceRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "经历不存在");
        }
        experienceRepository.deleteById(id);
    }
}
