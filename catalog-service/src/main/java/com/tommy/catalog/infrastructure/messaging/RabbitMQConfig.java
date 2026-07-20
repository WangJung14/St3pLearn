package com.tommy.catalog.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "course.events.exchange";
    public static final String COURSE_STATUS_ROUTING_KEY = "course.status.changed";
    public static final String COURSE_PUBLISHED_ROUTING_KEY = "course.published.key";

    @Bean
    public TopicExchange courseEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange catalogEventsExchange() {
        return new TopicExchange("catalog.events.exchange");
    }

    @Bean
    public org.springframework.amqp.core.TopicExchange adminExchange() {
        return new org.springframework.amqp.core.TopicExchange("admin.events.exchange");
    }

    @Bean
    public org.springframework.amqp.core.Queue catalogModerationQueue() {
        return new org.springframework.amqp.core.Queue("catalog.moderation.events.queue", true);
    }

    @Bean
    public org.springframework.amqp.core.Binding moderationResolvedBinding(org.springframework.amqp.core.Queue catalogModerationQueue, org.springframework.amqp.core.TopicExchange adminExchange) {
        return org.springframework.amqp.core.BindingBuilder.bind(catalogModerationQueue).to(adminExchange).with("moderation.resolved");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
