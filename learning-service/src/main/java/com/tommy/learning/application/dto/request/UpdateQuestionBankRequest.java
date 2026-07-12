package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateQuestionBankRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
