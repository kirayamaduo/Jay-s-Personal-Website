package com.jay.techportal.config;

import com.jay.techportal.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // 启用方法级别的安全注解 (@PreAuthorize, @Secured 等)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 启用 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 禁用 CSRF（因为我们使用 JWT，不需要 CSRF 保护）
                .csrf(AbstractHttpConfigurer::disable)

                // 使用无状态会话（JWT 不需要 session）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 公开访问的端点
                        .requestMatchers(
                                "/api/auth/**",           // 认证相关
                                "/api/public/**",         // 公开API
                                "/test/**"                // 测试接口
                        ).permitAll()

                        // 公开的 GET 请求（博客内容）
                        .requestMatchers(HttpMethod.GET,
                                "/api/projects/**",       // 项目
                                "/api/posts/**",          // 文章
                                "/api/tags/**",           // 标签
                                "/api/experiences/**",    // 经历
                                "/api/social-links/**"    // 社交链接
                        ).permitAll()

                        // 公开的 POST 请求（留言、评论）
                        .requestMatchers(HttpMethod.POST,
                                "/api/messages",          // 留言
                                "/api/posts/*/comments"   // 评论
                        ).permitAll()

                        // Swagger/Knife4j 文档（完整路径）
                        .requestMatchers(
                                "/doc.html",
                                "/doc.html/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // OPTIONS 请求放行（CORS 预检）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 管理接口需要认证
                        .requestMatchers("/api/admin/**").authenticated()

                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )

                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
