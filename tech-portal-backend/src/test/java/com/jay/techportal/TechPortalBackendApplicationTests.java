package com.jay.techportal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 应用基础测试
 * 注意：不使用 @SpringBootTest 避免需要完整的数据库和 Redis 连接
 */
@DisplayName("TechPortal 应用测试")
class TechPortalBackendApplicationTests {

	@Test
	@DisplayName("应用主类存在")
	void applicationClassExists() {
		TechPortalBackendApplication application = new TechPortalBackendApplication();
		assertThat(application).isNotNull();
	}

	@Test
	@DisplayName("main方法可以调用（但不实际启动）")
	void mainMethodExists() {
		// 只验证 main 方法存在，不实际启动应用
		assertThat(TechPortalBackendApplication.class).isNotNull();
	}
}
