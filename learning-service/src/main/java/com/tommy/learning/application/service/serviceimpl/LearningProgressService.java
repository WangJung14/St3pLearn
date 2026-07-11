package com.tommy.learning.application.service.serviceimpl;

import com.tommy.common.event.CourseCompletedEvent;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.learning.application.dto.request.UpdateProgressRequest;
import com.tommy.learning.application.dto.response.ResumeLearningResponse;
import com.tommy.learning.application.service.ILearningProgressService;
import com.tommy.learning.domain.entity.Enrollment;
import com.tommy.learning.domain.entity.LearningProgress;
import com.tommy.learning.domain.entity.LessonProgress;
import com.tommy.learning.domain.enums.EnrollmentStatus;
import com.tommy.learning.domain.enums.LessonStatus;
import com.tommy.learning.infrastructure.persistence.repository.EnrollmentRepository;
import com.tommy.learning.infrastructure.persistence.repository.LearningProgressRepository;
import com.tommy.learning.infrastructure.persistence.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final LearningProgressRepository learningProgressRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

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

    @Override
    @Transactional(readOnly = true)
    public ResumeLearningResponse resumeLearning(UUID studentId, UUID courseId) {
        // 1. Verify Enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_ENROLLED));
        
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new AppException(ErrorCode.COURSE_NOT_ENROLLED);
        }

        // 2. Read last_accessed_lesson_id from Redis first
        String lastAccessedKey = String.format("last_accessed:student:%s:course:%s", studentId, courseId);
        Object cachedLessonIdStr = redisTemplate.opsForValue().get(lastAccessedKey);
        
        UUID lessonId = null;
        if (cachedLessonIdStr != null) {
            lessonId = UUID.fromString(cachedLessonIdStr.toString());
        } else {
            // Fallback to DB
            lessonId = enrollment.getLastAccessedLessonId();
        }

        // Option A: If null, return null (never started)
        if (lessonId == null) {
            return ResumeLearningResponse.builder()
                    .lessonId(null)
                    .resumeAtSeconds(0)
                    .build();
        }

        // 3. Read watch_position_seconds from Redis first
        String progressKey = String.format(PROGRESS_KEY_PREFIX, studentId, courseId, lessonId);
        Object cachedProgress = redisTemplate.opsForValue().get(progressKey);
        
        int resumeAtSeconds = 0;
        if (cachedProgress != null) {
            resumeAtSeconds = Integer.parseInt(cachedProgress.toString());
        } else {
            // Fallback to DB
            LessonProgress lessonProgress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                    .orElse(null);
            if (lessonProgress != null) {
                resumeAtSeconds = lessonProgress.getWatchPositionSeconds();
            }
        }

        return ResumeLearningResponse.builder()
                .lessonId(lessonId)
                .resumeAtSeconds(resumeAtSeconds)
                .build();
    }

    @Override
    @Transactional
    public void completeLesson(UUID studentId, UUID courseId, UUID lessonId) {
        // 1. Check Enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_ENROLLED));
        
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new AppException(ErrorCode.COURSE_NOT_ENROLLED);
        }

        // 2. Fetch LessonProgress
        LessonProgress lessonProgress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // Idempotency check: if already complete, do nothing (return 200)
        if (lessonProgress.getStatus() == LessonStatus.COMPLETE) {
            log.info("Lesson {} is already complete for student {}. Ignoring request.", lessonId, studentId);
            return;
        }

        // Mark as COMPLETE
        lessonProgress.setStatus(LessonStatus.COMPLETE);
        lessonProgress.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(lessonProgress);

        // 3. Fetch LearningProgress with PESSIMISTIC_WRITE lock
        LearningProgress learningProgress = learningProgressRepository.findByEnrollmentIdWithLock(enrollment.getId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Should exist since enrollment exists

        // Increment completed lessons safely
        int newCompleted = learningProgress.getCompletedLessons() + 1;
        learningProgress.setCompletedLessons(newCompleted);

        // Calculate progress percentage
        int totalLessons = learningProgress.getTotalLessons();
        if (totalLessons > 0) {
            BigDecimal progress = BigDecimal.valueOf((double) newCompleted / totalLessons * 100)
                    .setScale(2, RoundingMode.HALF_UP);
            learningProgress.setProgressPercent(progress);
            
            // Sync to Enrollment as well
            enrollment.setProgressPercent(progress);
        }

        learningProgressRepository.save(learningProgress);
        enrollmentRepository.save(enrollment);

        log.info("Student {} completed lesson {}. Progress: {}/{} ({}%)", 
                studentId, lessonId, newCompleted, totalLessons, learningProgress.getProgressPercent());

        // 4. Trigger Phase 4 if 100% completed
        if (newCompleted == totalLessons) {
            log.info("Course {} is fully completed by student {}. Firing CourseCompletedEvent.", courseId, studentId);
            eventPublisher.publishEvent(CourseCompletedEvent.builder()
                    .studentId(studentId)
                    .courseId(courseId)
                    .enrollmentId(enrollment.getId())
                    .completedAt(LocalDateTime.now())
                    .build());
        }
    }
}
