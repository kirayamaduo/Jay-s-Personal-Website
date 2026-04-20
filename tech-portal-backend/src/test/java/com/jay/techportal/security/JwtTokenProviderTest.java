package com.jay.techportal.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtTokenProvider 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider 测试")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    // 测试用密钥（至少 32 字符用于 HS256）
    private static final String TEST_SECRET = "TestSecretKeyForJWTTokenGenerationMustBeAtLeast32CharactersLongForHS256";
    private static final long ACCESS_TOKEN_EXPIRATION = 900000L; // 15 分钟
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 天

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
        // 调用 init 方法初始化 key
        jwtTokenProvider.init();
    }

    @Nested
    @DisplayName("生成 Access Token 测试")
    class GenerateAccessTokenTests {

        @Test
        @DisplayName("通过 Authentication 生成 Access Token")
        void generateAccessToken_WithAuthentication() {
            // Given
            UserDetails userDetails = new User(
                    "admin",
                    "password",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // When
            String token = jwtTokenProvider.generateAccessToken(authentication);

            // Then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("通过用户名生成 Access Token")
        void generateAccessToken_WithUsername() {
            // When
            String token = jwtTokenProvider.generateAccessToken("admin");

            // Then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("admin");
        }

        @Test
        @DisplayName("Access Token 类型验证")
        void generateAccessToken_CheckType() {
            // When
            String token = jwtTokenProvider.generateAccessToken("admin");

            // Then
            assertThat(jwtTokenProvider.getTokenType(token)).isEqualTo("access");
        }
    }

    @Nested
    @DisplayName("生成 Refresh Token 测试")
    class GenerateRefreshTokenTests {

        @Test
        @DisplayName("成功生成 Refresh Token")
        void generateRefreshToken_Success() {
            // When
            String token = jwtTokenProvider.generateRefreshToken("admin");

            // Then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("Refresh Token 类型验证")
        void generateRefreshToken_CheckType() {
            // When
            String token = jwtTokenProvider.generateRefreshToken("admin");

            // Then
            assertThat(jwtTokenProvider.getTokenType(token)).isEqualTo("refresh");
        }

        @Test
        @DisplayName("Refresh Token 用户名验证")
        void generateRefreshToken_CheckUsername() {
            // When
            String token = jwtTokenProvider.generateRefreshToken("testuser");

            // Then
            assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("Token 验证测试")
    class ValidateTokenTests {

        @Test
        @DisplayName("验证有效的 Token")
        void validateToken_ValidToken() {
            // Given
            String token = jwtTokenProvider.generateAccessToken("admin");

            // When & Then
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("验证无效的 Token")
        void validateToken_InvalidToken() {
            // Given
            String invalidToken = "invalid.token.here";

            // When & Then
            assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
        }

        @Test
        @DisplayName("验证格式错误的 Token")
        void validateToken_MalformedToken() {
            // Given
            String malformedToken = "not-a-jwt-token";

            // When & Then
            assertThat(jwtTokenProvider.validateToken(malformedToken)).isFalse();
        }

        @Test
        @DisplayName("验证空 Token")
        void validateToken_EmptyToken() {
            // When & Then
            assertThat(jwtTokenProvider.validateToken("")).isFalse();
        }

        @Test
        @DisplayName("验证使用错误密钥签名的 Token")
        void validateToken_WrongSecret() {
            // Given - 创建一个使用不同密钥的 provider
            JwtTokenProvider otherProvider = new JwtTokenProvider();
            ReflectionTestUtils.setField(otherProvider, "jwtSecret", 
                    "DifferentSecretKeyForTestingPurposeMustBeAtLeast32Characters");
            ReflectionTestUtils.setField(otherProvider, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
            ReflectionTestUtils.setField(otherProvider, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
            otherProvider.init();

            String tokenFromOtherProvider = otherProvider.generateAccessToken("admin");

            // When & Then - 签名不匹配应返回 false
            assertThat(jwtTokenProvider.validateToken(tokenFromOtherProvider)).isFalse();
        }
    }

    @Nested
    @DisplayName("从 Token 获取信息测试")
    class GetInfoFromTokenTests {

        @Test
        @DisplayName("获取用户名")
        void getUsernameFromToken_Success() {
            // Given
            String token = jwtTokenProvider.generateAccessToken("testuser");

            // When
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Then
            assertThat(username).isEqualTo("testuser");
        }

        @Test
        @DisplayName("获取 Token 类型 - Access")
        void getTokenType_Access() {
            // Given
            String accessToken = jwtTokenProvider.generateAccessToken("admin");

            // When
            String type = jwtTokenProvider.getTokenType(accessToken);

            // Then
            assertThat(type).isEqualTo("access");
        }

        @Test
        @DisplayName("获取 Token 类型 - Refresh")
        void getTokenType_Refresh() {
            // Given
            String refreshToken = jwtTokenProvider.generateRefreshToken("admin");

            // When
            String type = jwtTokenProvider.getTokenType(refreshToken);

            // Then
            assertThat(type).isEqualTo("refresh");
        }
    }

    @Nested
    @DisplayName("Token 过期测试")
    class TokenExpirationTests {

        @Test
        @DisplayName("新生成的 Token 未过期")
        void isTokenExpired_FreshToken() {
            // Given
            String token = jwtTokenProvider.generateAccessToken("admin");

            // When & Then
            assertThat(jwtTokenProvider.isTokenExpired(token)).isFalse();
        }

        @Test
        @DisplayName("测试过期时间配置")
        void getAccessTokenExpiration_Success() {
            // When & Then
            assertThat(jwtTokenProvider.getAccessTokenExpiration()).isEqualTo(ACCESS_TOKEN_EXPIRATION);
        }

        @Test
        @DisplayName("测试 Refresh Token 过期时间配置")
        void getRefreshTokenExpiration_Success() {
            // When & Then
            assertThat(jwtTokenProvider.getRefreshTokenExpiration()).isEqualTo(REFRESH_TOKEN_EXPIRATION);
        }
    }

    @Nested
    @DisplayName("不同用户的 Token 测试")
    class DifferentUsersTokenTests {

        @Test
        @DisplayName("不同用户生成不同的 Token")
        void differentUsers_DifferentTokens() {
            // When
            String token1 = jwtTokenProvider.generateAccessToken("user1");
            String token2 = jwtTokenProvider.generateAccessToken("user2");

            // Then
            assertThat(token1).isNotEqualTo(token2);
            assertThat(jwtTokenProvider.getUsernameFromToken(token1)).isEqualTo("user1");
            assertThat(jwtTokenProvider.getUsernameFromToken(token2)).isEqualTo("user2");
        }

        @Test
        @DisplayName("同一用户生成的 Token 包含相同用户名")
        void sameUser_SameUsername() {
            // When
            String token1 = jwtTokenProvider.generateAccessToken("admin");
            String token2 = jwtTokenProvider.generateAccessToken("admin");

            // Then - 用户名相同
            assertThat(jwtTokenProvider.getUsernameFromToken(token1))
                    .isEqualTo(jwtTokenProvider.getUsernameFromToken(token2));
            // 两个 token 都是有效的
            assertThat(jwtTokenProvider.validateToken(token1)).isTrue();
            assertThat(jwtTokenProvider.validateToken(token2)).isTrue();
        }
    }

    @Nested
    @DisplayName("中文用户名测试")
    class ChineseUsernameTests {

        @Test
        @DisplayName("支持中文用户名")
        void chineseUsername_Success() {
            // Given
            String chineseUsername = "管理员";

            // When
            String token = jwtTokenProvider.generateAccessToken(chineseUsername);

            // Then
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
            assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo(chineseUsername);
        }

        @Test
        @DisplayName("支持特殊字符用户名")
        void specialCharUsername_Success() {
            // Given
            String specialUsername = "user@domain.com";

            // When
            String token = jwtTokenProvider.generateAccessToken(specialUsername);

            // Then
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
            assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo(specialUsername);
        }
    }
}
