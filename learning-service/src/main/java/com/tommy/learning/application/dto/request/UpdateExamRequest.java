package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateExamRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @Min(value = 0, message = "Passing score must be at least 0")
    private Double passingScore;

    @Min(value = 1, message = "Max attempts must be at least 1")
    private Integer maxAttempts;
}
