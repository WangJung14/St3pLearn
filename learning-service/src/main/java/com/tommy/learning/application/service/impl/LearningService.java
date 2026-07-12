package com.tommy.learning.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.learning.application.service.ILearningService;
import com.tommy.learning.domain.entity.CourseReplica;
import com.tommy.learning.domain.entity.Enrollment;
import com.tommy.learning.domain.entity.LearningProgress;
import com.tommy.learning.domain.entity.LessonProgress;
import com.tommy.learning.domain.enums.EnrollmentStatus;
import com.tommy.learning.domain.enums.LessonStatus;
import com.tommy.learning.infrastructure.persistence.repository.CourseReplicaRepository;
import com.tommy.learning.infrastructure.persistence.repository.EnrollmentRepository;
import com.tommy.learning.infrastructure.persistence.repository.LearningProgressRepository;
import com.tommy.learning.infrastructure.persistence.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningService implements ILearningService {
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProgressRepository learningProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseReplicaRepository courseReplicaRepository;

    @Override
    @Transactional
    public UUID startLearning(UUID studentId, UUID courseId){
        log.info("Student {} is starting learning course {}", studentId, courseId);

        // Verify if the student has enrolled in this course
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // If a progress bar has already been created, return the ID of the lesson you are currently studying
        if (learningProgressRepository.existsById(enrollment.getId())) {
            log.info("Progress already initialized for enrollment {}", enrollment.getId());
            return enrollment.getLastAccessedLessonId();
        }

        // Get course struct from Replica
        CourseReplica replica = courseReplicaRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<UUID> lessonIds = replica.getLessonIds();
        if (lessonIds == null || lessonIds.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_CONTENT_REQUIRED);
        }

        // Init Overview Learning Progress
        LearningProgress learningProgress = LearningProgress.builder()
                .enrollment(enrollment)
                .totalLessons(replica.getTotalLessons())
                .totalDuration(replica.getTotalDuration())
                .completedLessons(0)
                .watchedDuration(0)
                .progressPercent(BigDecimal.ZERO)
                .build();

        learningProgressRepository.save(learningProgress);

        // Init detail Learning Progress
        List<LessonProgress> lessonProgresses = lessonIds.stream()
                .map(lessonId -> LessonProgress.builder()
                        .enrollmentId(enrollment.getId())
                        .lessonId(lessonId)
                        .status(LessonStatus.NOT_STARTED)
                        .watchPositionSeconds(0)
                        .build())
                .toList();

        lessonProgressRepository.saveAll(lessonProgresses);

        // Mark the first lesson and update the enrollment status
        UUID firstLessonId = lessonIds.get(0);
        enrollment.setLastAccessedLessonId(firstLessonId);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        enrollmentRepository.save(enrollment);

        log.info("Successfully initialized learning progress for student {} in course {}", studentId, courseId);

        return firstLessonId;
    }

}
