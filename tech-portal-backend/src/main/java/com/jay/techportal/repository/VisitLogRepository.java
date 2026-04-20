package com.jay.techportal.repository;

import com.jay.techportal.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 访问日志 Repository
 */
@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    /**
     * 根据时间范围查找访问记录
     */
    List<VisitLog> findByVisitedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据路径查找访问记录
     */
    List<VisitLog> findByPathStartingWith(String pathPrefix);

    /**
     * 统计指定时间范围内的访问量
     */
    long countByVisitedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 统计指定路径的访问量
     */
    long countByPath(String path);

    /**
     * 获取热门页面
     */
    @Query("SELECT v.path, COUNT(v) as cnt FROM VisitLog v " +
           "WHERE v.visitedAt >= :since " +
           "GROUP BY v.path ORDER BY cnt DESC")
    List<Object[]> findPopularPages(@Param("since") LocalDateTime since);

    /**
     * 按设备类型统计
     */
    @Query("SELECT v.deviceType, COUNT(v) FROM VisitLog v " +
           "WHERE v.visitedAt >= :since " +
           "GROUP BY v.deviceType")
    List<Object[]> countByDeviceType(@Param("since") LocalDateTime since);

    /**
     * 按日期统计访问量
     */
    @Query("SELECT FUNCTION('DATE', v.visitedAt) as visitDate, COUNT(v) FROM VisitLog v " +
           "WHERE v.visitedAt >= :since " +
           "GROUP BY FUNCTION('DATE', v.visitedAt) " +
           "ORDER BY visitDate")
    List<Object[]> countByDate(@Param("since") LocalDateTime since);
}
