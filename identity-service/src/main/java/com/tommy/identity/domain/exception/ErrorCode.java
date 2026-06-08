package com.tommy.identity.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(500, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected system error occurred. Please try again later"),
    INVALID_KEY(400, HttpStatus.BAD_REQUEST, "Invalid configuration data"),

    // Các lỗi liên quan đến Auth / User
    USER_EXISTED(409, HttpStatus.CONFLICT, "This username already exists"),
    EMAIL_EXISTED(409, HttpStatus.CONFLICT, "This email address is already registered"),
    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "User not found"),
    UNAUTHENTICATED(401, HttpStatus.UNAUTHORIZED, "Authentication required or token has expired"),
    ROLE_NOT_FOUND(500, HttpStatus.INTERNAL_SERVER_ERROR, "System error: configured role not found"),

    //login
    INVALID_CREDENTIALS(401, HttpStatus.UNAUTHORIZED, "Email or Password is invalid"),
    ACCOUNT_LOCKED(403, HttpStatus.FORBIDDEN, "Account is locked"),;

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
