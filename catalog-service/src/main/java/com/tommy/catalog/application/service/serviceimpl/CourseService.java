package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.*;
import com.tommy.catalog.application.dto.response.*;
import com.tommy.catalog.application.service.ICourseService;
import com.tommy.catalog.domain.entity.*;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.catalog.infrastructure.persistence.CourseSpecification;
import com.tommy.common.event.CoursePublishedEvent;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.catalog.infrastructure.persistence.repository.*;
import com.tommy.catalog.util.SlugUtil;
import com.tommy.catalog.infrastructure.messaging.RabbitMQConfig;
import com.tommy.common.event.CourseStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService implements ICourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CourseLessonRepository  courseLessonRepository;
    private final CourseApprovalRequestRepository courseApprovalRequestRepository;
    private final CourseChapterRepository courseChapterRepository;
    private final RabbitTemplate rabbitTemplate;

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

        publishStatusChangeEvent(savedCourse);

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
        
        publishStatusChangeEvent(course);
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

        // 5. Block Race Condition , multi submitted
        boolean hasPendingTicket = courseApprovalRequestRepository.existsByCourseIdAndStatus(courseId, "PENDING");
        if (hasPendingTicket) {
            log.warn("Course {} already has a PENDING ticket", courseId);
            throw new AppException(ErrorCode.COURSE_ALREADY_SUBMITTED);
        }
        // 5. Update status of course
        course.setStatus(CourseStatus.PENDING_REVIEW);
        courseRepository.save(course);
        
        publishStatusChangeEvent(course);

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
        
        publishStatusChangeEvent(course);

        log.info("Admin {} processed approval ticket {} with action: {}", adminId, requestId, action);
    }

    /*
    * Get pending course approvals
    * */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseApprovalResponse> getPendingApprovals(int page, int size) {
        // Sort by ascending because ADMIN SHOULD approval first in first out
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").ascending());
        return courseApprovalRequestRepository.findApprovalsByStatus("PENDING", pageable);
    }

    /*
    * Get approval course Detail
    * */
    @Override
    @Transactional(readOnly = true)
    public CourseApprovalDetailResponse getApprovalDetail(UUID requestId) {

        //1. Find ticket by id
        CourseApprovalRequest request = courseApprovalRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.APPROVAL_REQUEST_NOT_FOUND));

        // 2. Get course detail
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 3. Count the lesson of course
        int lessonCount = courseLessonRepository.countLessonsByCourseId(course.getId());

        // 4. return
        return CourseApprovalDetailResponse.builder()
                .approvalRequestId(request.getId())
                .ticketStatus(request.getStatus())
                .submittedBy(request.getSubmittedBy())
                .submittedAt(request.getSubmittedAt())
                .courseInfo(course)
                .totalLessons(lessonCount)
                .build();
    }

    /*
    * Search course
    * */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseCardResponse> searchPublicCourses(CourseSearchRequest request) {
        // 1. The course muse be PUBLISHED
        Specification<Course> spec = Specification.where(CourseSpecification.isPublished());

        // 2. Check all filter conditions
        if (org.springframework.util.StringUtils.hasText(request.getKeyword())) {
            spec = spec.and(CourseSpecification.hasTitleLike(request.getKeyword()));
        }

        if (org.springframework.util.StringUtils.hasText(request.getLevel())) {
            spec = spec.and(CourseSpecification.hasLevel(request.getLevel()));
        }

        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            spec = spec.and(CourseSpecification.isPriceInRange(request.getMinPrice(), request.getMaxPrice()));
        }

        if (request.getCategoryId() != null) {
            spec = spec.and(CourseSpecification.hasCategoryId(request.getCategoryId()));
        }

        // 3. Dynamic Sorting processing
        Sort.Direction direction = Sort.Direction.fromString(request.getSortDir().toUpperCase());
        Sort sort = Sort.by(direction, request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // 4. Create Paging object
        Page<Course> coursePage = courseRepository.findAll(spec, pageable);

        return coursePage.map(course -> CourseCardResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())
                .build()
        );
    }

    /*
    * Get a list of courses that the instructor has offered
    * */
    @Override
    @Transactional(readOnly = true)
    public Page<Course> getMyCourses(UUID instructorId, int page, int size) {
        // Sort in descending order by creation time ( Newest at the top)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return courseRepository.findByInstructorId(instructorId, pageable);
    }

    @Override
    @Transactional
    public void cancelCourseApproval(UUID courseId, UUID instructorId) {
        // 1. Find course by id
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Verified ownership
        boolean isOwnership = instructorId.equals(course.getInstructorId());
        if (!isOwnership) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 3. Make sure this course is in pending approval status
        boolean isCourseInPending = CourseStatus.PENDING_REVIEW.equals(course.getStatus());
        if (!isCourseInPending) {
            throw new AppException(ErrorCode.INVALID_TICKET_STATUS);
        }

        // 4. Find ticket is pending
        CourseApprovalRequest pendingTicket = courseApprovalRequestRepository.findByCourseIdAndStatus(courseId, "PENDING")
                .orElseThrow(() -> new AppException(ErrorCode.APPROVAL_REQUEST_NOT_FOUND));

        // 5. Recall request, change status from PENDING to DRAFT and set ticket status is CANCELED
        course.setStatus(CourseStatus.DRAFT);
        pendingTicket.setStatus("CANCELED");

        courseRepository.save(course);
        courseApprovalRequestRepository.save(pendingTicket);
        
        publishStatusChangeEvent(course);

        log.info("Instructor {} successfully canceled the approval request for course {}", instructorId, courseId);
    }

    @Override
    @Transactional
    public void publishCourse(UUID courseId, UUID instructorId) {
        // 1. Find course by id
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Verified ownership
        boolean isOwnership = instructorId.equals(course.getInstructorId());
        if(!isOwnership) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 3. Make sure this course has been approved
        boolean isCourseHasBeenApproved = CourseStatus.APPROVED.equals(course.getStatus());
        if (!isCourseHasBeenApproved) {
            throw new AppException(ErrorCode.COURSE_NOT_APPROVED_PUBLISH);
        }

        // 4. Change status to PUBLISHED
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);

        // 5. Get the list of Chapters sorted by order
        int totalLesson = 0;
        int totalDuring = 0;
        List<UUID> orderedLessonIds = new ArrayList<>();

        List<CourseChapter> chapters = courseChapterRepository.findByCourseIdOrderByDisplayOrderAsc(course.getId());
        for(CourseChapter chapter : chapters) {
            List<CourseLesson> lessons = courseLessonRepository.findByChapterIdOrderByDisplayOrderAsc(chapter.getId());
            // Get the arranged list of Lessons of each Chapter
            for(CourseLesson lesson : lessons) {
                totalLesson++;
                totalDuring += (lesson.getDurationSeconds() != null ? lesson.getDurationSeconds() : 0);
                orderedLessonIds.add(lesson.getId());
            }
        }

        // 6. Push event to learning service
        try{
            CoursePublishedEvent event = new CoursePublishedEvent(
                    course.getId(),
                    course.getStatus().name(),
                    totalLesson,
                    totalDuring,
                    orderedLessonIds
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.COURSE_PUBLISHED_ROUTING_KEY,
                    event
            );
        }catch (AppException e){
            log.error("Failed to publish CoursePublishedEvent for course {}: {}", course.getId(), e.getMessage());
        }
        log.info("Instructor {} successfully published course {}", instructorId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "courseDetail", key = "#slug")
    public CourseDetailPublicResponse getPublicCourseDetail(String slug) {
        // 1. Find course by slug , just get PUBLISH COURSE
        Course course = courseRepository.findBySlugAndStatus(slug, CourseStatus.PUBLISHED)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Get all chapter of course
        List<CourseChapter> chapters = courseChapterRepository.findByCourseIdOrderByDisplayOrderAsc(course.getId());

        // 3. Create list of chapter DTOs
        List<ChapterPublicDto> chapterDtos = chapters.stream().map(chapter -> {

            // Get all lesson of this chapter
            List<CourseLesson> lessons = courseLessonRepository.findByChapterIdOrderByDisplayOrderAsc(chapter.getId());

            // Mapping and Masking lesson
            List<LessonPublicDto> lessonDtos = lessons.stream().map(lesson -> {
                LessonPublicDto dto = LessonPublicDto.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .orderIndex(lesson.getDisplayOrder())
                        .duration(lesson.getDurationSeconds())
                        .isPreview(lesson.getIsPreview())
                        .build();
                if(lesson.getIsPreview()){
                    if (lesson.getContent() != null) {
                        dto.setVideoUrl(lesson.getContent().getStorageUrl());
                    }
                }else{
                    dto.setVideoUrl(null);
                }
                return dto;
        }).toList();
            return ChapterPublicDto.builder()
                    .id(chapter.getId())
                    .title(chapter.getTitle())
                    .orderIndex(chapter.getDisplayOrder())
                    .lessons(lessonDtos)
                    .build();
        }).toList();

        return CourseDetailPublicResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getShortDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())
                .curriculum(chapterDtos)
                .build();
    }

    /*
    * Get course summary
    * */
    @Override
    @Transactional(readOnly = true)
    public List<CourseSummaryResponse> getCourseSummaries(List<UUID> courseIds){
        log.info("Fetching bulk summaries for {} courses", courseIds.size());

        if(courseIds == null || courseIds.isEmpty()){
            return Collections.emptyList();
        }

        List<Course> courses = courseRepository.findAllById(courseIds);

        return courses.stream().map(course -> CourseSummaryResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .instructorId(course.getInstructorId())
                .build()
        ).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public void migrateCourseStatuses() {
        List<Course> allCourses = courseRepository.findAll();
        for (Course course : allCourses) {
            publishStatusChangeEvent(course);
        }
        log.info("Migrated {} course statuses to Learning Service", allCourses.size());
    }

    private void publishStatusChangeEvent(Course course) {
        try {
            CourseStatusChangedEvent event = new CourseStatusChangedEvent(course.getId(), course.getStatus().name());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.COURSE_STATUS_ROUTING_KEY, event);
            log.info("Published status change event for course {}: {}", course.getId(), course.getStatus());
        } catch (Exception e) {
            log.error("Failed to publish status change event for course {}: {}", course.getId(), e.getMessage());
        }
    }
}