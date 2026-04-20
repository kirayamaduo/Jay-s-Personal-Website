package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 访问日志实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "visit_logs")
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(length = 500)
    private String referrer;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(length = 50)
    private String country;

    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "visited_at", insertable = false, updatable = false)
    private LocalDateTime visitedAt;
}
