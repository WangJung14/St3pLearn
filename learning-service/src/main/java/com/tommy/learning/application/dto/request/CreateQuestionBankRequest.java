package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateQuestionBankRequest {

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
