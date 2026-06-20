package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.CourseTaxonomyRequest;
import com.tommy.catalog.application.dto.request.CreateCourseRequest;
import com.tommy.catalog.application.dto.request.ProcessApprovalRequest;
import com.tommy.catalog.application.dto.request.UpdateCourseRequest;
import com.tommy.catalog.application.service.ICourseService;
import com.tommy.catalog.domain.entity.Category;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseApprovalRequest;
import com.tommy.catalog.domain.entity.Tag;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.catalog.infrastructure.persistence.repository.*;
import com.tommy.catalog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService implements ICourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CourseLessonRepository  courseLessonRepository;
    private final CourseApprovalRequestRepository courseApprovalRequestRepository;

    @Override
    @Transactional
    public Course createCourse(UUID instructorId, CreateCourseRequest request) {

        // 1. Turn the Title into a basic Slug
        String baseSlug = SlugUtil.toSlug(request.getTitle());
        String uniqueSlug = baseSlug;

        // 2. The algorithm checks and assigns numbers if duplicates
        int counter = 1;
        while (courseRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }

        // 3. Return entity
        Course course = Course.builder()
                .instructorId(instructorId) // Get teacher id
                .title(request.getTitle())
                .slug(uniqueSlug)
                .shortDescription(request.getShortDescription())
                .level(request.getLevel())
                .language(request.getLanguage())
                .price(request.getPrice())
                .status(CourseStatus.DRAFT)
                .build();

        // 4. Save to database
        Course savedCourse = courseRepository.save(course);
        log.info("Teacher {} created new draft course: {}", instructorId, savedCourse.getSlug());

        return savedCourse;
    }

    /*
    * Update course
    * */
    @Override
    @Transactional
    public Course updateCourse(UUID courseId, UUID instructorId, UpdateCourseRequest request) {
        // 1. Find course by id
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Check id of the course creator and editor.
        if (!course.getInstructorId().equals(instructorId)) {
            throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
        }

        // 3. Update data
        course.setTitle(request.getTitle());
        course.setShortDescription(request.getShortDescription());
        course.setLevel(request.getLevel());
        course.setLanguage(request.getLanguage());
        course.setPrice(request.getPrice());

        // 4. Save to database
        Course updatedCourse = courseRepository.save(course);
        log.info("Instructor {} successfully updated course: {}", instructorId, courseId);

        return updatedCourse;
    }

    /*
    * Get course by id
    * */
    @Override
    @Transactional(readOnly = true)
    public Course getCourseById(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return course;
    }

    /*
    * Get All course for admin
    * */
    @Override
    @Transactional(readOnly = true)
    public Page<Course> getAllCoursesForAdmin(int page , int size){
        // sort by creation date (createAt) in descending order
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> courses = courseRepository.findAll(pageable);

        return courses;
    }

    /*
    * Archive course (soft delete)
    * */

    @Override
    @Transactional
    public void archiveCourse(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 1. Check ownership
        boolean isOwner = course.getInstructorId().equals(instructorId);
        if(!isOwner){
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 2. Change status to ARCHIVED
        course.setStatus(CourseStatus.ARCHIVED);

        courseRepository.save(course);
        log.info("Instructor {} archived course: {}", instructorId, courseId);
    }

    /*
    * Assign category and tag for course
    * */
    @Override
    @Transactional
    public Course assignCategoriesAndTags(UUID courseId, UUID instructorId, CourseTaxonomyRequest request){
        // 1. Find course by id
        Course course =  courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Validate ownership
        boolean isOwner = course.getInstructorId().equals(instructorId);
        if(!isOwner){
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 3. Handling categories
        List<Category> validCategories = categoryRepository.findAllById(request.getCategoryIds());
        if(validCategories.isEmpty()){
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        course.setCategories(new HashSet<>(validCategories));

        // 4. Handling tags
        boolean isValidTag = request.getTagIds() != null && !request.getTagIds().isEmpty();
        if (isValidTag) {
            List<Tag> validTags = tagRepository.findAllById(request.getTagIds());
            course.setTags(new HashSet<>(validTags));
        } else {
            course.getTags().clear(); // if empty clear all tag
        }
        return  courseRepository.save(course);
    }

    /*
    * Submit course for admin approve
    * */
    @Override
    @Transactional
    public void submitCourseForApproval(UUID courseId, UUID instructorId){

        // 1. Find course by id
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Validate ownership of course
        boolean isOwner = instructorId.equals(course.getInstructorId());
        if(!isOwner){
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 3. Only allow courses with DRAFT or REJECTED status to be submitted
        boolean canBeSubmitted = CourseStatus.DRAFT.equals(course.getStatus())
                || CourseStatus.REJECTED.equals(course.getStatus());
        if(!canBeSubmitted){
            throw new AppException(ErrorCode.COURSE_CANNOT_BE_SUBMITTED);
        }

        // 4. Block empty content. If this course doesn't have any lesson , handle error and block it
        int lessonCount = courseLessonRepository.countLessonsByCourseId(courseId);
        if(lessonCount == 0){
            log.warn("Instructor {} attempted to submit an empty course {}", instructorId, courseId);
            throw new AppException(ErrorCode.COURSE_CONTENT_REQUIRED);
        }

        // 5. Update status of course
        course.setStatus(CourseStatus.PENDING_REVIEW);
        courseRepository.save(course);

        // 6. Create ticket for admin
        CourseApprovalRequest approvalRequest = CourseApprovalRequest.builder()
                .courseId(courseId)
                .submittedBy(instructorId)
                .status("PENDING")
                .build();

       courseApprovalRequestRepository.save(approvalRequest);

       log.info("Course {} successfully submitted for approval by instructor {}", courseId, instructorId);
    }

    /*
    * Process course Approvel for ADMIn
    * */

    @Override
    @Transactional
    public void processCourseApproval(UUID requestId, UUID adminId, ProcessApprovalRequest request){
        // 1. Find ticket by status PENDING
        CourseApprovalRequest approvalTicket = courseApprovalRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.APPROVAL_REQUEST_NOT_FOUND));

        // Check status
        boolean isPending = "PENDING".equals(approvalTicket.getStatus());
        if(!isPending){
            throw new AppException(ErrorCode.INVALID_TICKET_STATUS);
        }

        // 2. Make sure this course also pending approvel
        Course course = courseRepository.findById(approvalTicket.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Check status
        boolean isCoursePending = CourseStatus.PENDING_REVIEW.equals(course.getStatus());
        if (!isCoursePending) {
            throw new AppException(ErrorCode.INVALID_TICKET_STATUS);
        }


        String action = request.getAction().toUpperCase();

        switch (action) {
            case "APPROVE" -> {
                approvalTicket.setStatus("APPROVED");
                course.setStatus(CourseStatus.APPROVED);
            }

            case "REJECT" -> {
                if (request.getReviewNote() == null || request.getReviewNote().trim().isEmpty()) {
                    throw new AppException(ErrorCode.REVIEW_NOTE_REQUIRED);
                }

                approvalTicket.setStatus("REJECTED");
                approvalTicket.setReviewNote(request.getReviewNote());
                course.setStatus(CourseStatus.REJECTED);
            }

            default -> throw new AppException(ErrorCode.INVALID_APPROVAL_ACTION);
        }

        approvalTicket.setReviewerId(adminId);
        approvalTicket.setReviewedAt(java.time.LocalDateTime.now());

        courseApprovalRequestRepository.save(approvalTicket);
        courseRepository.save(course);

        log.info("Admin {} processed approval ticket {} with action: {}", adminId, requestId, action);
    }

}