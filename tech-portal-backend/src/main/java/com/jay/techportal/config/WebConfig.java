package com.jay.techportal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Web MVC 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:}")
    private String additionalAllowedOrigins;

    /**
     * 静态资源处理（Knife4j 文档需要）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Knife4j / Swagger 静态资源
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * CORS 配置源
     * 这个 Bean 会被 Spring Security 使用
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = new ArrayList<>(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        ));
        if (additionalAllowedOrigins != null && !additionalAllowedOrigins.isBlank()) {
            Arrays.stream(additionalAllowedOrigins.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(origins::add);
        }
        configuration.setAllowedOrigins(origins);

        // 允许的 HTTP 方法
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));

        // 暴露的响应头（前端可以访问）
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // 允许携带凭证（cookies、authorization headers）
        configuration.setAllowCredentials(true);

        // 预检请求缓存时间（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}