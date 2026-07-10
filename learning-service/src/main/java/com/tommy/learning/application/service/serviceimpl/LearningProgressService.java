package com.tommy.learning.application.service.serviceimpl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.learning.application.dto.request.UpdateProgressRequest;
import com.tommy.learning.application.service.ILearningProgressService;
import com.tommy.learning.domain.entity.Enrollment;
import com.tommy.learning.domain.entity.LessonProgress;
import com.tommy.learning.domain.enums.EnrollmentStatus;
import com.tommy.learning.infrastructure.persistence.repository.EnrollmentRepository;
import com.tommy.learning.infrastructure.persistence.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningProgressService implements ILearningProgressService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PROGRESS_KEY_PREFIX = "progress:student:%s:course:%s:lesson:%s";

    @Override
    @Transactional(readOnly = true)
    public void trackProgress(UUID studentId, UUID courseId, UUID lessonId, UpdateProgressRequest request) {
        // 1. Verify Enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_ENROLLED));
        
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new AppException(ErrorCode.COURSE_NOT_ENROLLED);
        }

        // 2. Verify LessonProgress exists
        LessonProgress lessonProgress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // 3. Save to Redis cache instead of DB directly to prevent bottleneck
        String key = String.format(PROGRESS_KEY_PREFIX, studentId, courseId, lessonId);
        redisTemplate.opsForValue().set(key, request.getCurrentSeconds());
        
        // At this point we just cached it. The CronJob will pick it up and save to DB
        // Optionally update lastAccessedLessonId directly to Redis as well to prevent DB locking
        String lastAccessedKey = String.format("last_accessed:student:%s:course:%s", studentId, courseId);
        redisTemplate.opsForValue().set(lastAccessedKey, lessonId.toString());
        
        log.debug("Tracking progress to Redis for student: {}, lesson: {}, seconds: {}", studentId, lessonId, request.getCurrentSeconds());
    }
}
