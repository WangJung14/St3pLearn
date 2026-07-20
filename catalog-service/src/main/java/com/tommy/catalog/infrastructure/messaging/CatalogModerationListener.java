package com.tommy.catalog.infrastructure.messaging;

import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import com.tommy.common.event.ModerationResolvedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogModerationListener {

    private final CourseRepository courseRepository;

    @RabbitListener(queues = "catalog.moderation.events.queue")
    @Transactional
    public void handleModerationResolvedEvent(ModerationResolvedEvent event) {
        log.info("Catalog received ModerationResolvedEvent: {}", event);

        if ("COURSE".equalsIgnoreCase(event.getTargetType())) {
            if ("HIDE_COURSE".equalsIgnoreCase(event.getAction())) {
                UUID courseId = UUID.fromString(event.getTargetId());
                courseRepository.findById(courseId).ifPresent(course -> {
                    course.setStatus(CourseStatus.DRAFT); // Or a specific status like ARCHIVED/BANNED
                    courseRepository.save(course);
                    log.info("Hidden course {} due to ModerationCase {}", courseId, event.getCaseId());
                });
            }
        }
    }
}
