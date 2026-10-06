package com.duong.RestaurantManagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
class RestaurantManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
