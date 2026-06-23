package com.tommy.catalog.application.service.serviceimpl;


import com.tommy.catalog.application.dto.request.ReplyReviewRequest;
import com.tommy.catalog.application.dto.request.SubmitReviewRequest;
import com.tommy.catalog.application.dto.response.ReviewReplyResponse;
import com.tommy.catalog.application.dto.response.ReviewResponse;
import com.tommy.catalog.application.service.ICourseReviewService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseReview;
import com.tommy.catalog.domain.entity.ReviewReply;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import com.tommy.catalog.infrastructure.persistence.repository.CourseReviewRepository;
import com.tommy.catalog.infrastructure.persistence.repository.ReviewReplyRepository;
import com.tommy.catalog.infrastructure.persistence.repository.StudentEnrolledCourseRepository;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ReviewReplyRepository replyRepository;

    /*
    * Create review course
    * */

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

    /*
    * View all review course
    * */
    @Override
    public Page<ReviewResponse> getCourseReviews(UUID courseId, Pageable pageable) {
        return reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId, pageable)
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .studentId(review.getStudentId())
                        .rating(review.getRating())
                        .reviewText(review.getReviewText())
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getUpdatedAt())
                        .build());
    }

    /*
    * User update review course
    * */
    @Override
    @Transactional
    public ReviewResponse updateReview(UUID studentId, UUID courseId, UUID reviewId, SubmitReviewRequest request) {
        // 1. Find review by id
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getStudentId().equals(studentId) || !review.getCourseId().equals(courseId)) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 2. Lock the course entry to recalculate your score.
        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 3. Ccoring algorithm when updated
        BigDecimal total = BigDecimal.valueOf(course.getTotalReviews());
        BigDecimal currentAvg = course.getAvgRating();
        BigDecimal oldRating = BigDecimal.valueOf(review.getRating());
        BigDecimal newRating = BigDecimal.valueOf(request.getRating());

        // Current total score = Avg * Total
        BigDecimal totalScore = currentAvg.multiply(total);
        // New total score  = Old total score - old user score + new user score
        BigDecimal newTotalScore = totalScore.subtract(oldRating).add(newRating);
        // New average rating (Number of reviews remains the same)
        BigDecimal newAvgRating = newTotalScore.divide(total, 2, RoundingMode.HALF_UP);

        // 4. Save
        course.setAvgRating(newAvgRating);
        courseRepository.save(course);

        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        reviewRepository.save(review);

        return ReviewResponse.builder()
                .id(review.getId())
                .studentId(review.getStudentId())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    /*
    * User delete review course
    * */
    @Override
    @Transactional
    public void deleteReview(UUID studentId, UUID courseId, UUID reviewId) {
        // 1. find review and check role
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!review.getStudentId().equals(studentId) || !review.getCourseId().equals(courseId)) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 2.Lock the course entry to recalculate your score
        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));


        int newTotalCount = course.getTotalReviews() - 1;
        BigDecimal newAvgRating = BigDecimal.ZERO;

        if (newTotalCount > 0) {
            BigDecimal total = BigDecimal.valueOf(course.getTotalReviews());
            BigDecimal currentAvg = course.getAvgRating();
            BigDecimal deletedRating = BigDecimal.valueOf(review.getRating());


            BigDecimal totalScore = currentAvg.multiply(total);

            BigDecimal newTotalScore = totalScore.subtract(deletedRating);

            newAvgRating = newTotalScore.divide(BigDecimal.valueOf(newTotalCount), 2, RoundingMode.HALF_UP);
        }

        course.setTotalReviews(newTotalCount);
        course.setAvgRating(newAvgRating);
        courseRepository.save(course);

        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public ReviewReplyResponse replyToReview(UUID teacherId, UUID courseId, UUID reviewId, ReplyReviewRequest request) {
        // 1. Check if review exist and matches course
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getCourseId().equals(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        // 2.Only the course instructor is allowed to reply
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getInstructorId().equals(teacherId)) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 3. Each review can only be answered once
        if (replyRepository.findByReviewId(reviewId).isPresent()) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 4. save
        ReviewReply reply = ReviewReply.builder()
                .reviewId(reviewId)
                .authorId(teacherId)
                .content(request.getContent())
                .build();

        replyRepository.save(reply);

        return ReviewReplyResponse.builder()
                .id(reply.getId())
                .reviewId(reply.getReviewId())
                .authorId(reply.getAuthorId())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .build();
    }


}
