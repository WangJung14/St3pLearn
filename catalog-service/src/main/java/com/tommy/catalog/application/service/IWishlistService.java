package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.response.CourseCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IWishlistService {

    // Save course
    public void saveCourseToWishlist(UUID studentId, UUID courseId);

    // Remove course from wishlist
    public void removeCourseFromWishlist(UUID studentId, UUID courseId);

    // View saved courses
    public Page<CourseCardResponse> getStudentWishlist(UUID studentId, Pageable pageable);
}
