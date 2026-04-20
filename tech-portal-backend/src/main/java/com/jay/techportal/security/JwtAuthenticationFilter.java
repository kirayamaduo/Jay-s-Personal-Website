package com.jay.techportal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 每次请求都会执行，从请求头中提取 JWT Token 并验证
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 从请求头中获取 JWT Token
            String jwt = getJwtFromRequest(request);

            // 验证 Token 并设置认证信息
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // 检查是否是 access token
                String tokenType = jwtTokenProvider.getTokenType(jwt);
                if (!"access".equals(tokenType)) {
                    log.warn("Invalid token type for authentication: {}", tokenType);
                    filterChain.doFilter(request, response);
                    return;
                }

                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                // 加载用户信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set authentication for user: {}", username);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 JWT Token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
