package com.tommy.payment.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_ORDER_COMPLETED_QUEUE = "payment.order.completed.queue";
    public static final String PAYMENT_ORDER_COMPLETED_ROUTING_KEY = "payment.order.completed";

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentOrderCompletedQueue() {
        return new Queue(PAYMENT_ORDER_COMPLETED_QUEUE, true);
    }

    @Bean
    public Binding bindingPaymentOrderCompleted(Queue paymentOrderCompletedQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentOrderCompletedQueue)
                .to(paymentExchange)
                .with(PAYMENT_ORDER_COMPLETED_ROUTING_KEY);
    }
}
