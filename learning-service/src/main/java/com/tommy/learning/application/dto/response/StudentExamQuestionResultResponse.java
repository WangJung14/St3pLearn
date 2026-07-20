package com.tommy.learning.application.dto.response;

import com.tommy.learning.domain.entity.json.QuestionMetadata;
import com.tommy.learning.domain.entity.json.StudentAnswer;
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
public class StudentExamQuestionResultResponse {
    private UUID questionId;
    private QuestionType type;
    private String content;
    private QuestionMetadata metadata;
    private Double points;

    // Student's data
    private StudentAnswer studentAnswer;
    private Double score;
    private String feedback;
}
