package com.s2.todo.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.s2.todo.backend.model")
@EnableJpaRepositories(basePackages = "com.s2.todo.backend.repository")
@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
		assert true;
	}

}
