package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.response.CourseCardResponse;
import com.tommy.catalog.application.service.IWishlistService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseWishlist;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import com.tommy.catalog.infrastructure.persistence.repository.CourseWishlistRepository;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService implements IWishlistService {

    private final CourseRepository courseRepository;
    private final CourseWishlistRepository courseWishlistRepository;

    /*
    * Save course to wishlist
    * */
    @Override
    @Transactional
    public void saveCourseToWishlist(UUID studentId, UUID courseId) {
        // 1. Make sure course exists and is PUBLISHED
        Course course = courseRepository.findById(courseId)
                .filter(c -> CourseStatus.PUBLISHED.equals(c.getStatus()))
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Check if student has previously interacted with course
        Optional<CourseWishlist> existingWishlist = courseWishlistRepository.findByStudentIdAndCourseId(studentId, courseId);

        if(existingWishlist.isPresent()) {
            CourseWishlist wishlist = existingWishlist.get();

            // If they previously Soft delete, they can now Like again -> Reactivate
            if(!wishlist.getIsActive()) {
                wishlist.setIsActive(true);
                wishlist.setPriceAtAdded(course.getPrice());
                courseWishlistRepository.save(wishlist);
                log.info("Student {} re-added course {} to wishlist", studentId, courseId);
            }
        }else{
            // If this is the first time saving course
            CourseWishlist newWishList = CourseWishlist.builder()
                    .studentId(studentId)
                    .courseId(courseId)
                    .priceAtAdded(course.getPrice())
                    .isActive(true)
                    .build();
            courseWishlistRepository.save(newWishList);
            log.info("Student {} added course {} to wishlist for the first time", studentId, courseId);
        }
    }

    /*
    * Remove course from wishlist
    * */
    @Override
    @Transactional
    public void removeCourseFromWishlist(UUID studentId, UUID courseId) {
        courseWishlistRepository.findByStudentIdAndCourseId(studentId, courseId)
                .ifPresent(courseWishlist -> {
                    if(courseWishlist.getIsActive()) {
                        courseWishlist.setIsActive(false);
                        courseWishlistRepository.save(courseWishlist);
                        log.info("Student {} removed course {} from wishlist", studentId, courseId);
                    }
                });
    }

    /*
    * Get all courses added to wishlist
    * */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseCardResponse> getStudentWishlist(UUID studentId, Pageable pageable) {
        // 1. Find active course of this student
        Page<CourseWishlist> wishlistPage = courseWishlistRepository
                .findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId,pageable);

        // 2. Mapping from CourseWishList to CourseCardResponse
        return wishlistPage.map(
                wishlist ->{
                    Course course = courseRepository.findById(wishlist.getCourseId())
                            .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

                    return CourseCardResponse.builder()
                            .id(course.getId())
                            .title(course.getTitle())
                            .slug(course.getSlug())
                            .thumbnailUrl(course.getThumbnailUrl())
                            .price(course.getPrice())
                            .avgRating(course.getAvgRating())
                            .totalReviews(course.getTotalReviews())
                            .build();
                }
        );
    }
}
