package com.jay.techportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "social_links")
public class SocialLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String platform;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false)
    private String url;

    @Column(length = 100)
    private String username;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
