package com.tommy.common.exception;

import com.tommy.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Lỗi hệ thống chưa được bắt (500)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(Exception exception) {
        ErrorCode errorCode = ErrorCode.UNCATCHED_EXCEPTION;
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // 2. Lỗi nghiệp vụ tự định nghĩa (AppException)
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // 3. Lỗi Validation (@Valid)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getBindingResult().getFieldError() != null 
                ? exception.getBindingResult().getFieldError().getDefaultMessage() 
                : "INVALID_KEY";
        
        ErrorCode errorCode = ErrorCode.INVALID_KEY; // Default
        try {
            if (enumKey != null) {
                errorCode = ErrorCode.valueOf(enumKey);
            }
        } catch (IllegalArgumentException e) {
            // Keep default
        }

        String errorMessage = (errorCode == ErrorCode.INVALID_KEY && exception.getBindingResult().getFieldError() != null)
                ? exception.getBindingResult().getFieldError().getDefaultMessage()
                : errorCode.getMessage();

        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorMessage);
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}
