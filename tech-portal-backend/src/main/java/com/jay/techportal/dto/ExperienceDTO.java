package com.jay.techportal.dto;

import com.jay.techportal.entity.Experience;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "经历DTO")
public class ExperienceDTO {

    @Schema(description = "经历ID", example = "1")
    private Long id;

    @Schema(description = "类型（work/education）", example = "work")
    private String type;

    @Schema(description = "职位/学位", example = "高级软件工程师")
    private String title;

    @Schema(description = "公司/学校", example = "某科技公司")
    private String company;

    @Schema(description = "地点", example = "北京")
    private String location;

    @Schema(description = "开始日期", example = "2020-01-01")
    private LocalDate startDate;

    @Schema(description = "结束日期（null表示至今）", example = "2023-12-31")
    private LocalDate endDate;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "成就列表")
    private List<String> achievements;

    @Schema(description = "排序顺序", example = "0")
    private Integer displayOrder;

    /**
     * 从 Entity 转换为 DTO
     */
    public static ExperienceDTO fromEntity(Experience experience) {
        if (experience == null) {
            return null;
        }
        return new ExperienceDTO(
                experience.getId(),
                experience.getType(),
                experience.getTitle(),
                experience.getCompany(),
                experience.getLocation(),
                experience.getStartDate(),
                experience.getEndDate(),
                experience.getDescription(),
                experience.getAchievements(),
                experience.getDisplayOrder()
        );
    }
}
