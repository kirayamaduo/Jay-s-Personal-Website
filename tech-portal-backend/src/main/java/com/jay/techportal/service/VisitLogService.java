package com.jay.techportal.service;

import com.jay.techportal.entity.VisitLog;
import com.jay.techportal.repository.VisitLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 访问日志 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VisitLogService {

    private final VisitLogRepository visitLogRepository;

    /**
     * 异步记录访问日志
     */
    @Async
    public void logVisit(String path, HttpServletRequest request) {
        try {
            VisitLog visitLog = VisitLog.builder()
                    .path(path)
                    .referrer(request.getHeader("Referer"))
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .deviceType(detectDeviceType(request.getHeader("User-Agent")))
                    .build();

            visitLogRepository.save(visitLog);
        } catch (Exception e) {
            log.error("Failed to log visit: {}", e.getMessage());
        }
    }

    /**
     * 获取今日访问量
     */
    public long getTodayVisitCount() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return visitLogRepository.countByVisitedAtBetween(startOfDay, now);
    }

    /**
     * 获取总访问量
     */
    public long getTotalVisitCount() {
        return visitLogRepository.count();
    }

    /**
     * 获取指定天数内的访问统计
     */
    public Map<String, Object> getVisitStats(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVisits", visitLogRepository.countByVisitedAtBetween(since, LocalDateTime.now()));

        // 按日期统计
        List<Object[]> dailyStats = visitLogRepository.countByDate(since);
        stats.put("dailyStats", dailyStats.stream()
                .map(row -> Map.of("date", row[0].toString(), "count", row[1]))
                .collect(Collectors.toList()));

        // 热门页面
        List<Object[]> popularPages = visitLogRepository.findPopularPages(since);
        stats.put("popularPages", popularPages.stream()
                .limit(10)
                .map(row -> Map.of("path", row[0], "count", row[1]))
                .collect(Collectors.toList()));

        // 设备类型统计
        List<Object[]> deviceStats = visitLogRepository.countByDeviceType(since);
        stats.put("deviceStats", deviceStats.stream()
                .map(row -> Map.of("type", row[0] != null ? row[0] : "unknown", "count", row[1]))
                .collect(Collectors.toList()));

        return stats;
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 检测设备类型
     */
    private String detectDeviceType(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("mobile") || userAgent.contains("android") ||
            userAgent.contains("iphone") || userAgent.contains("ipod")) {
            return "mobile";
        }
        if (userAgent.contains("ipad") || userAgent.contains("tablet")) {
            return "tablet";
        }
        if (userAgent.contains("bot") || userAgent.contains("spider") ||
            userAgent.contains("crawler")) {
            return "bot";
        }
        return "desktop";
    }
}
