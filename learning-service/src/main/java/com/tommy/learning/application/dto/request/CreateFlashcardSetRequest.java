package com.tommy.learning.application.dto.request;

import com.tommy.learning.domain.enums.VisibilityEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateFlashcardSetRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private UUID courseId;

    @NotNull(message = "Visibility is required")
    private VisibilityEnum visibility;
}
