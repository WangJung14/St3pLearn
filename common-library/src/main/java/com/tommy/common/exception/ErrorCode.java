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
    COURSE_NOT_FOUND(404, "Course not found.", HttpStatus.NOT_FOUND),
    COURSE_NOT_APPROVED(403, "This course has not been approved yet.", HttpStatus.FORBIDDEN),
    COURSE_ALREADY_ARCHIVED(409, "This course has already been archived.", HttpStatus.CONFLICT),
    COURSE_ACCESS_DENIED(403, "You do not have permission to access this course.", HttpStatus.FORBIDDEN),
    COURSE_CANNOT_BE_SUBMITTED(409, "Only courses with DRAFT or REJECTED status can be submitted.", HttpStatus.CONFLICT),
    COURSE_CONTENT_REQUIRED(400, "The course must contain at least one chapter and one lesson before submission.", HttpStatus.BAD_REQUEST),

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
