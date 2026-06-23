package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.SubmitReviewRequest;
import com.tommy.catalog.application.dto.response.ReviewResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICourseReviewService {

    // Review course
    ReviewResponse submitReview(UUID studentId, UUID courseId, SubmitReviewRequest request);

    // View review course
    Page<ReviewResponse> getCourseReviews(UUID courseId, Pageable pageable);

    // update review course for student
    ReviewResponse updateReview(UUID studentId, UUID courseId, UUID reviewId, SubmitReviewRequest request);

    // delete review course for student
    void deleteReview(UUID studentId, UUID courseId, UUID reviewId);

}
