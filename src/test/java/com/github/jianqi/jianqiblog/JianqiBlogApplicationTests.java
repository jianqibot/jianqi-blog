package com.github.jianqi.jianqiblog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JianqiBlogApplication.class, webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test.properties"})
class JianqiBlogApplicationTests {

    @Test
    void contextLoads() {
    }
}
