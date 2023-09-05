package com.s2.todo.backend;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

@EntityScan("com.s2.todo.backend.model")
@EnableJpaRepositories(basePackages = "com.s2.todo.backend.repository")
@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        assert true;
    }

}
