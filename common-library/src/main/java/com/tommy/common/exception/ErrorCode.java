package com.tommy.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // System errors
    INVALID_KEY(400, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
    FORBIDDEN_ROLE(403, "You do not have the required role to access this resource", HttpStatus.FORBIDDEN),
    UNCATCHED_EXCEPTION(500, "Uncatched exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION(500, "An unexpected system error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Course errors
    COURSE_ALREADY_EXISTS(409, "A course with this title already exists.", HttpStatus.CONFLICT),
    COMMENT_NOT_FOUND(1202, "Comment not found", HttpStatus.NOT_FOUND),

    // Learning
    COURSE_NOT_ENROLLED(1301, "You are not enrolled in this course", HttpStatus.FORBIDDEN),
    COURSE_NOT_FOUND(404, "Course not found.", HttpStatus.NOT_FOUND),
    COURSE_NOT_APPROVED(403, "This course has not been approved yet.", HttpStatus.FORBIDDEN),
    COURSE_ALREADY_ARCHIVED(409, "This course has already been archived.", HttpStatus.CONFLICT),
    COURSE_ACCESS_DENIED(403, "You do not have permission to access this course.", HttpStatus.FORBIDDEN),
    COURSE_CANNOT_BE_SUBMITTED(409, "Only courses with DRAFT or REJECTED status can be submitted.", HttpStatus.CONFLICT),
    COURSE_CONTENT_REQUIRED(400, "The course must contain at least one chapter and one lesson before submission.", HttpStatus.BAD_REQUEST),
    COURSE_ALREADY_SUBMITTED(400, "This course has already been submitted yet",HttpStatus.CONFLICT),
    COURSE_NOT_APPROVED_PUBLISH(400,"This course has not been reviewed. Only approved courses can be published",HttpStatus.BAD_REQUEST),
    COURSE_NOT_PUBLISHED(400, "This course is not published yet.", HttpStatus.BAD_REQUEST),

    // Category errors
    CATEGORY_NOT_FOUND(404, "Category not found.", HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS(409, "A category with this name already exists.", HttpStatus.CONFLICT),

    // Tag errors
    TAG_NOT_FOUND(404, "Tag not found.", HttpStatus.NOT_FOUND),
    TAG_ALREADY_EXISTS(409, "A tag with this name already exists.", HttpStatus.CONFLICT),

    // Chapter errors
    CHAPTER_NOT_FOUND(404, "Chapter not found.", HttpStatus.NOT_FOUND),
    CHAPTER_ACCESS_DENIED(403, "This chapter does not belong to the specified course.", HttpStatus.FORBIDDEN),

    // Lesson errors
    LESSON_NOT_FOUND(404, "Lesson not found.", HttpStatus.NOT_FOUND),

    // Review errors
    REVIEW_NOT_FOUND(404, "Review not found.", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS(409, "You have already submitted a review for this course.", HttpStatus.CONFLICT),
    REVIEW_NOT_ALLOWED(403, "Only enrolled students can review this course.", HttpStatus.FORBIDDEN),
    REPLY_ALREADY_EXISTS(400,"The reply already exists", HttpStatus.BAD_REQUEST),

    // Approval course error
    APPROVAL_REQUEST_NOT_FOUND(404, "Approval request not found.", HttpStatus.NOT_FOUND),
    INVALID_TICKET_STATUS(400, "This approval request has already been processed.", HttpStatus.BAD_REQUEST),
    INVALID_APPROVAL_ACTION(400, "Invalid approval action. Only APPROVE or REJECT is allowed.", HttpStatus.BAD_REQUEST),
    REVIEW_NOTE_REQUIRED(400, "A review note is required when rejecting a course.", HttpStatus.BAD_REQUEST),


    /// LEARNING ERROR
    // Enroll Course
    ENROLLMENT_EXISTS(400,"This enrollment already exists.", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(404 ,"This enrollment does not exist.", HttpStatus.NOT_FOUND),

    // Report
    REPORT_NOT_FOUND(404, "Report not found.", HttpStatus.NOT_FOUND),
    
    // Question Bank
    QUESTION_BANK_NOT_FOUND(404, "Question bank not found.", HttpStatus.NOT_FOUND),
    QUESTION_BANK_ACCESS_DENIED(403, "You do not have permission to modify this question bank.", HttpStatus.FORBIDDEN),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
