package com.tommy.learning.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "course.events.exchange";
    public static final String SYNC_QUEUE_NAME = "learning.course.sync.queue";
    public static final String COURSE_STATUS_ROUTING_KEY = "course.status.changed";

    @Bean
    public TopicExchange courseEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue courseSyncQueue() {
        return new Queue(SYNC_QUEUE_NAME, true); // durable queue
    }

    @Bean
    public Binding courseSyncBinding(Queue courseSyncQueue, TopicExchange courseEventsExchange) {
        return BindingBuilder.bind(courseSyncQueue).to(courseEventsExchange).with(COURSE_STATUS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}