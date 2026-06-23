package com.tommy.learning.infrastructure.messaging.consumer;

import com.tommy.common.event.CourseStatusChangedEvent;
import com.tommy.learning.domain.entity.CourseReplica;
import com.tommy.learning.infrastructure.messaging.RabbitMQConfig;
import com.tommy.learning.infrastructure.persistence.repository.CourseReplicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseEventConsumer {

    private final CourseReplicaRepository courseReplicaRepository;

    @RabbitListener(queues = RabbitMQConfig.SYNC_QUEUE_NAME)
    public void handleCourseStatusChangedEvent(CourseStatusChangedEvent event) {
        log.info("Received CourseStatusChangedEvent for course: {}, status: {}", event.courseId(), event.status());

        CourseReplica replica = courseReplicaRepository.findById(event.courseId())
                .orElse(CourseReplica.builder().id(event.courseId()).build());
        
        replica.setStatus(event.status());
        courseReplicaRepository.save(replica);
        
        log.info("Successfully updated CourseReplica for course: {}", event.courseId());
    }
}
