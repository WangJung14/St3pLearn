package com.tommy.learning.job;

import com.tommy.learning.domain.entity.Enrollment;
import com.tommy.learning.domain.entity.LessonProgress;
import com.tommy.learning.domain.enums.LessonStatus;
import com.tommy.learning.infrastructure.persistence.repository.EnrollmentRepository;
import com.tommy.learning.infrastructure.persistence.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProgressSyncJob {

    private final RedisTemplate<String, Object> redisTemplate;
    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;

    private static final String PROGRESS_KEY_PATTERN = "progress:student:*:course:*:lesson:*";

    // Run every 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncProgressFromRedisToDb() {
        log.info("Starting ProgressSyncJob...");

        Set<String> keys = redisTemplate.keys(PROGRESS_KEY_PATTERN);
        if (keys == null || keys.isEmpty()) {
            log.info("No progress data to sync.");
            return;
        }

        for (String key : keys) {
            try {
                // key format: progress:student:{studentId}:course:{courseId}:lesson:{lessonId}
                String[] parts = key.split(":");
                UUID studentId = UUID.fromString(parts[2]);
                UUID courseId = UUID.fromString(parts[4]);
                UUID lessonId = UUID.fromString(parts[6]);

                Object value = redisTemplate.opsForValue().get(key);
                if (value == null) continue;

                int currentSeconds = Integer.parseInt(value.toString());

                // 1. Check Enrollment
                Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
                if (enrollmentOpt.isEmpty()) {
                    redisTemplate.delete(key);
                    continue;
                }
                Enrollment enrollment = enrollmentOpt.get();

                // 2. Check LessonProgress
                Optional<LessonProgress> lessonProgressOpt = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId);
                if (lessonProgressOpt.isPresent()) {
                    LessonProgress lessonProgress = lessonProgressOpt.get();

                    // Step 3 (Crucial): Check currentSeconds > watchPositionSeconds
                    if (currentSeconds > lessonProgress.getWatchPositionSeconds()) {
                        lessonProgress.setWatchPositionSeconds(currentSeconds);

                        // Step 4: If NOT_STARTED, change to IN_PROGRESS
                        if (lessonProgress.getStatus() == LessonStatus.NOT_STARTED) {
                            lessonProgress.setStatus(LessonStatus.IN_PROGRESS);
                        }

                        lessonProgressRepository.save(lessonProgress);

                        // Step 5: Update Enrollment last_accessed
                        enrollment.setLastAccessedLessonId(lessonId);
                        enrollment.setLastAccessedAt(LocalDateTime.now());
                        enrollmentRepository.save(enrollment);
                        
                        log.debug("Synced progress for student {}, lesson {} to {}s", studentId, lessonId, currentSeconds);
                    }
                }
                
                // Remove from Redis after sync
                redisTemplate.delete(key);

            } catch (Exception e) {
                log.error("Failed to sync progress for key {}: {}", key, e.getMessage());
            }
        }
        
        log.info("Finished ProgressSyncJob. Synced {} records.", keys.size());
    }
}
