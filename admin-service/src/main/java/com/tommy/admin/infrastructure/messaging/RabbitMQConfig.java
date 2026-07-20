package com.tommy.admin.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String IDENTITY_EXCHANGE = "identity.events.exchange";
    public static final String COURSE_EXCHANGE = "course.events.exchange";
    public static final String COURSE_COMPLETED_EXCHANGE = "course.completed.exchange";
    public static final String CATALOG_EXCHANGE = "catalog.events.exchange";
    public static final String ADMIN_EXCHANGE = "admin.events.exchange";

    // Queues
    public static final String ADMIN_USER_EVENTS_QUEUE = "admin.user.events.queue";
    public static final String ADMIN_COURSE_EVENTS_QUEUE = "admin.course.events.queue";
    public static final String ADMIN_PAYMENT_EVENTS_QUEUE = "admin.payment.events.queue";
    public static final String ADMIN_REPORT_EVENTS_QUEUE = "admin.report.events.queue";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Exchanges
    @Bean
    public TopicExchange identityExchange() {
        return new TopicExchange(IDENTITY_EXCHANGE);
    }

    @Bean
    public TopicExchange courseExchange() {
        return new TopicExchange(COURSE_EXCHANGE);
    }

    @Bean
    public FanoutExchange courseCompletedExchange() {
        return new FanoutExchange(COURSE_COMPLETED_EXCHANGE);
    }

    @Bean
    public TopicExchange catalogExchange() {
        return new TopicExchange(CATALOG_EXCHANGE);
    }

    @Bean
    public TopicExchange adminExchange() {
        return new TopicExchange(ADMIN_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue adminUserEventsQueue() {
        return new Queue(ADMIN_USER_EVENTS_QUEUE, true);
    }

    @Bean
    public Queue adminCourseEventsQueue() {
        return new Queue(ADMIN_COURSE_EVENTS_QUEUE, true);
    }

    @Bean
    public Queue adminPaymentEventsQueue() {
        return new Queue(ADMIN_PAYMENT_EVENTS_QUEUE, true);
    }

    @Bean
    public Queue adminReportEventsQueue() {
        return new Queue(ADMIN_REPORT_EVENTS_QUEUE, true);
    }

    public static final String ADMIN_AUDIT_EVENTS_QUEUE = "admin.audit.events.queue";

    @Bean
    public Queue adminAuditEventsQueue() {
        return new Queue(ADMIN_AUDIT_EVENTS_QUEUE, true);
    }

    // Bindings
    @Bean
    public Binding auditEventsBinding(Queue adminAuditEventsQueue, TopicExchange adminExchange) {
        return BindingBuilder.bind(adminAuditEventsQueue).to(adminExchange).with("system.audit");
    }

    @Bean
    public Binding reportSubmittedBinding(Queue adminReportEventsQueue, TopicExchange catalogExchange) {
        return BindingBuilder.bind(adminReportEventsQueue).to(catalogExchange).with("report.submitted");
    }

    @Bean
    public Binding userRegisteredBinding(Queue adminUserEventsQueue, TopicExchange identityExchange) {
        return BindingBuilder.bind(adminUserEventsQueue).to(identityExchange).with("user.registered");
    }

    @Bean
    public Binding userLoggedInBinding(Queue adminUserEventsQueue, TopicExchange identityExchange) {
        return BindingBuilder.bind(adminUserEventsQueue).to(identityExchange).with("user.logged_in");
    }

    @Bean
    public Binding courseEnrolledBinding(Queue adminCourseEventsQueue, TopicExchange courseExchange) {
        return BindingBuilder.bind(adminCourseEventsQueue).to(courseExchange).with("course.enrolled.key");
    }

    @Bean
    public Binding courseCompletedBinding(Queue adminCourseEventsQueue, FanoutExchange courseCompletedExchange) {
        return BindingBuilder.bind(adminCourseEventsQueue).to(courseCompletedExchange);
    }
}
