package com.tommy.catalog.domain.exception;



import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // System errors
    UNCATEGORIZED_EXCEPTION(500,HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected system error occurred. Please try again later."),

    INVALID_KEY(400, HttpStatus.BAD_REQUEST, "Invalid configuration data."),

    // Authentication & Authorization errors
    UNAUTHENTICATED(401, HttpStatus.UNAUTHORIZED, "Authentication required or token has expired."),

    FORBIDDEN_ROLE(403, HttpStatus.FORBIDDEN, "Only Teachers or Administrators are allowed to perform this action."),

    // Course errors
    COURSE_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "A course with this title already exists."),

    COURSE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Course not found."),

    COURSE_NOT_APPROVED(403, HttpStatus.FORBIDDEN, "This course has not been approved yet."),

    COURSE_ALREADY_ARCHIVED(409, HttpStatus.CONFLICT, "This course has already been archived."),

    // Category errors
    CATEGORY_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Category not found."),

    CATEGORY_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "A category with this name already exists."),

    // Tag errors
    TAG_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Tag not found."),

    TAG_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "A tag with this name already exists."),

    // Chapter errors
    CHAPTER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Chapter not found."),

    // Lesson errors
    LESSON_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Lesson not found."),

    // Review errors
    REVIEW_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Review not found."),

    REVIEW_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "You have already submitted a review for this course."),

    REVIEW_NOT_ALLOWED(403, HttpStatus.FORBIDDEN, "Only enrolled students can review this course.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
