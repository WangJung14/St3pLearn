package com.tommy.catalog.application.service.serviceimpl;


import com.tommy.catalog.application.dto.request.SubmitReviewRequest;
import com.tommy.catalog.application.dto.response.ReviewResponse;
import com.tommy.catalog.application.service.ICourseReviewService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseReview;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import com.tommy.catalog.infrastructure.persistence.repository.CourseReviewRepository;
import com.tommy.catalog.infrastructure.persistence.repository.StudentEnrolledCourseRepository;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CousreReviewService implements ICourseReviewService {

    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final StudentEnrolledCourseRepository enrolledCourseRepository;

    @Override
    @Transactional
    public ReviewResponse submitReview(UUID studentId, UUID courseId, SubmitReviewRequest request) {
        log.info("Student {} is submitting a review for course {}", studentId, courseId);
        // 1. Check if student already owns the course
        if (!enrolledCourseRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.warn("Student {} attempted to review un-enrolled course {}", studentId, courseId);
            throw new AppException(ErrorCode.REVIEW_NOT_ALLOWED);
        }

        // 2. Block duplicate review
        if (reviewRepository.findByCourseIdAndStudentId(courseId, studentId).isPresent()) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 3. Course data stream lock for exclusive scoring
        Course course = courseRepository.findByIdForUpdate(courseId)
                .filter(c -> CourseStatus.PUBLISHED.equals(c.getStatus()))
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 4. AGGREGATION ALGORITHM USING BIGDECIMAL
        BigDecimal oldTotal = BigDecimal.valueOf(course.getTotalReviews());
        BigDecimal oldAvg = course.getAvgRating() != null ? course.getAvgRating() : BigDecimal.ZERO;
        BigDecimal newRating = BigDecimal.valueOf(request.getRating());

        // Old total score = (oldAvg * oldTotal)
        BigDecimal oldTotalScore = oldAvg.multiply(oldTotal);

        // New total score  = Old total score + newRating
        BigDecimal newTotalScore = oldTotalScore.add(newRating);

        // New total review
        int newTotalCount = course.getTotalReviews() + 1;

        // New average score = New total score / New number of reviews (Rounded to 2 decimal places)
        BigDecimal newAvgRating = newTotalScore.divide(BigDecimal.valueOf(newTotalCount), 2, RoundingMode.HALF_UP);

        // 5. Update course
        course.setTotalReviews(newTotalCount);
        course.setAvgRating(newAvgRating);
        courseRepository.save(course);

        // 6. Save review
        CourseReview review = CourseReview.builder()
                .courseId(courseId)
                .studentId(studentId)
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .build();
        reviewRepository.save(review);

        log.info("Successfully calculated new rating {} for course {}", newAvgRating, courseId);

        return ReviewResponse.builder()
                .id(review.getId())
                .studentId(review.getStudentId())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .build();
    }


}
