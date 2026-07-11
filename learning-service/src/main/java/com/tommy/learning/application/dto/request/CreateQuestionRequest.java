package com.tommy.learning.application.dto.request;

import com.tommy.learning.domain.entity.json.QuestionMetadata;
import com.tommy.learning.domain.enums.QuestionDifficulty;
import com.tommy.learning.domain.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQuestionRequest {

    @NotNull(message = "Type is required")
    private QuestionType type;

    @NotBlank(message = "Content is required")
    private String content;

    private QuestionMetadata metadata;

    private QuestionDifficulty difficulty;

    private Double points = 1.0;
}
