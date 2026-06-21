package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.dto.response.CourseCardResponse;
import com.tommy.catalog.application.service.IWishlistService;
import com.tommy.common.security.RequireRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {
    private final IWishlistService wishlistService;

    // Save course to wishlist
    @PostMapping("/course/{courseId}")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Void>> saveCourse(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID studentId)
    {
        log.info("Student {} requested to save course {} to wishlist", studentId, courseId);
        wishlistService.saveCourseToWishlist(studentId,courseId);

        return ResponseEntity.ok(ApiResponse.success(200, "The course has been saved to your wishlist", null));
    }

    // Remove course from wishlist
    @DeleteMapping("/courses/{courseId}")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Void>> removeCourse(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID studentId)
    {
        log.info("Student {} requested to remove course {} from wishlist", studentId, courseId);
        wishlistService.removeCourseFromWishlist(studentId,courseId);

        return ResponseEntity.ok(ApiResponse.success(200, "Course has been cancelled", null));
    }

    // View saved courses
    @GetMapping
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Page<CourseCardResponse>>> getMyWishlist(
            @RequestHeader("X-User-Id") UUID studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Student {} fetching wishlist page: {}, size: {}", studentId, page, size);
        Page<CourseCardResponse> result = wishlistService.getStudentWishlist(
                studentId,
                PageRequest.of(page, size)
        );

        return ResponseEntity.ok(ApiResponse.success(200, "Successfully create a list of your favorite courses", result));
    }


}
