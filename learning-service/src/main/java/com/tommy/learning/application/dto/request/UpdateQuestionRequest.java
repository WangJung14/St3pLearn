package com.tommy.learning.application.dto.request;

import com.tommy.learning.domain.entity.json.QuestionMetadata;
import com.tommy.learning.domain.enums.QuestionDifficulty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateQuestionRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private QuestionMetadata metadata;

    private QuestionDifficulty difficulty;

    private Double points;
}
