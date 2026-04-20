package com.jay.techportal.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 配置类
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 自定义 ObjectMapper，支持 Java 8 时间类型
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        // 支持 Java 8 时间类型（LocalDateTime 等）
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * 配置 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 JSON 序列化
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(createObjectMapper());

        // Key 使用 String 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 使用 JSON 序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 CacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // JSON 序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(createObjectMapper());

        // 默认缓存配置（1小时过期）
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // 不同缓存的自定义配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 项目缓存：2小时
        cacheConfigurations.put("projects", defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigurations.put("project", defaultConfig.entryTtl(Duration.ofHours(2)));

        // 文章缓存：30分钟（更新较频繁）
        cacheConfigurations.put("posts", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("post", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 标签缓存：6小时（很少更新）
        cacheConfigurations.put("tags", defaultConfig.entryTtl(Duration.ofHours(6)));

        // 经历缓存：6小时
        cacheConfigurations.put("experiences", defaultConfig.entryTtl(Duration.ofHours(6)));

        // 社交链接缓存：6小时
        cacheConfigurations.put("socialLinks", defaultConfig.entryTtl(Duration.ofHours(6)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
