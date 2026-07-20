package com.tommy.identity.infrastructure.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ADMIN_EXCHANGE = "admin.events.exchange";
    public static final String IDENTITY_MODERATION_QUEUE = "identity.moderation.events.queue";

    @Bean
    public org.springframework.amqp.core.TopicExchange adminExchange() {
        return new org.springframework.amqp.core.TopicExchange(ADMIN_EXCHANGE);
    }

    @Bean
    public org.springframework.amqp.core.Queue identityModerationQueue() {
        return new org.springframework.amqp.core.Queue(IDENTITY_MODERATION_QUEUE, true);
    }

    @Bean
    public org.springframework.amqp.core.Binding moderationResolvedBinding(org.springframework.amqp.core.Queue identityModerationQueue, org.springframework.amqp.core.TopicExchange adminExchange) {
        return org.springframework.amqp.core.BindingBuilder.bind(identityModerationQueue).to(adminExchange).with("moderation.resolved");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
