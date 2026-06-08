package com.tommy.identity.presentation.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // if data is null , it will not appear in JSON
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    // Return message "Success" and data
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Success")
                .data(data)
                .build();
    }
    // Return message
    public static <T> ApiResponse<T> success(int code , String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .build();
    }

    // return error
    public static <T> ApiResponse<T> error(int code , String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
