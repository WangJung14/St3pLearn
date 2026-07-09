package com.tommy.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FORGOT_PASSWORD_QUEUE = "forgot_password_queue";
    public static final String AUTH_EXCHANGE = "auth_exchange";
    public static final String FORGOT_PASSWORD_ROUTING_KEY = "forgot_password_routing_key";

    @Bean
    public Queue forgotPasswordQueue() {
        return new Queue(FORGOT_PASSWORD_QUEUE, true);
    }

    @Bean
    public DirectExchange authExchange() {
        return new DirectExchange(AUTH_EXCHANGE);
    }

    @Bean
    public Binding bindingForgotPasswordQueue(Queue forgotPasswordQueue, DirectExchange authExchange) {
        return BindingBuilder.bind(forgotPasswordQueue).to(authExchange).with(FORGOT_PASSWORD_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
