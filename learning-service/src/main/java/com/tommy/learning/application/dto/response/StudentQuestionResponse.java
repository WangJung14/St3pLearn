package com.tommy.learning.application.dto.response;

import com.tommy.learning.domain.entity.json.QuestionMetadata;
import com.tommy.learning.domain.enums.QuestionDifficulty;
import com.tommy.learning.domain.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentQuestionResponse {
    private UUID id;
    private QuestionType type;
    private String content;
    private QuestionMetadata metadata;
    private QuestionDifficulty difficulty;
    private Double points;
}
