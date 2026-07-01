package com.tommy.learning.infrastructure.messaging.consumer;

import com.tommy.common.event.CoursePublishedEvent;
import com.tommy.common.event.CourseStatusChangedEvent;
import com.tommy.learning.application.service.impl.CourseSyncService;
import com.tommy.learning.domain.entity.CourseReplica;
import com.tommy.learning.infrastructure.messaging.RabbitMQConfig;
import com.tommy.learning.infrastructure.persistence.repository.CourseReplicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseEventConsumer {

    private final CourseSyncService courseSyncService;

    // listener published course
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "learning.course.published.queue", durable = "true"),
            exchange = @Exchange(value = "course.events.exchange", type = "topic"),
            key = "course.published.key"
    ))

    public void handleCoursePublishedEvent(CoursePublishedEvent event) {
        log.info("RabbitMQ vớt được CoursePublishedEvent của khóa học: {}", event.courseId());
        try {
            courseSyncService.syncPublishedCourse(event);
        } catch (Exception e) {
            log.error("Lỗi đồng bộ CoursePublishedEvent cho khóa {}: {}", event.courseId(), e.getMessage());
            throw e; // Ném lỗi để RabbitMQ biết mà không xóa message (phục vụ retry sau này)
        }
    }

    // listener small case like "change status,..."
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "learning.course.status.sync.queue", durable = "true"),
            exchange = @Exchange(value = "course.events.exchange", type = "topic"),
            key = "course.status.changed"
    ))
    public void handleCourseStatusChangedEvent(CourseStatusChangedEvent event) {
        log.info("RabbitMQ vớt được CourseStatusChangedEvent của khóa học: {}, status: {}", event.courseId(), event.status());
        try {
            courseSyncService.syncCourseStatus(event);
        } catch (Exception e) {
            log.error("Lỗi đồng bộ CourseStatusChangedEvent cho khóa {}: {}", event.courseId(), e.getMessage());
            throw e;
        }
    }
}
