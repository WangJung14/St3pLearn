package com.tommy.learning;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration")
class LearningApplicationTests {
    @Test
    void contextLoads() {
    }
    @MockBean
    private RabbitTemplate rabbitTemplate;
}
