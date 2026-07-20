package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateExamRequest {

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Passing score is required")
    @Min(value = 0, message = "Passing score must be at least 0")
    private Double passingScore;

    @Min(value = 1, message = "Max attempts must be at least 1")
    private Integer maxAttempts;

    @NotNull(message = "Questions list cannot be null")
    private List<UUID> questionIds;
}
