package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.SubmitReviewRequest;
import com.tommy.catalog.application.dto.response.ReviewResponse;

import java.util.UUID;

public interface ICourseReviewService {
    ReviewResponse submitReview(UUID studentId, UUID courseId, SubmitReviewRequest request);
}
