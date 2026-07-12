package com.tommy.learning.application.service.impl;

import com.tommy.common.event.CoursePublishedEvent;
import com.tommy.common.event.CourseStatusChangedEvent;
import com.tommy.learning.application.service.ICourseSyncService;
import com.tommy.learning.domain.entity.CourseReplica;
import com.tommy.learning.infrastructure.persistence.repository.CourseReplicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSyncService implements ICourseSyncService {

    private final CourseReplicaRepository courseReplicaRepository;

    @Override
    @Transactional
    public void syncPublishedCourse(CoursePublishedEvent event) {
        log.info("Bắt đầu đồng bộ dữ liệu khóa học: {}", event.courseId());

        // Check if the course is already in the db; if not, create a new one.
        CourseReplica replica = courseReplicaRepository.findById(event.courseId())
                .orElseGet(() -> CourseReplica.builder()
                        .id(event.courseId())
                        .build());

        // update info
        replica.setStatus(event.status());
        replica.setTotalLessons(event.totalLessons());
        replica.setTotalDuration(event.totalDuration());
        replica.setLessonIds(event.lessonIds());

        courseReplicaRepository.save(replica);
        log.info("Đồng bộ thành công khóa học {} với {} bài học vào Learning DB", event.courseId(), event.totalLessons());
    }

    @Override
    @Transactional
    public void syncCourseStatus(CourseStatusChangedEvent event){
        CourseReplica replica = courseReplicaRepository.findById(event.courseId())
                .orElseGet(() -> CourseReplica.builder()
                        .id(event.courseId())
                        .build());

        replica.setStatus(event.status());
        courseReplicaRepository.save(replica);
        log.info("Cập nhật thành công status {} cho khóa học {}", event.status(), event.courseId());
    }
}
