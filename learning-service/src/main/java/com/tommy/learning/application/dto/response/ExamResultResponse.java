package com.tommy.learning.application.dto.response;

import com.tommy.learning.domain.enums.ExamAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResultResponse {
    private UUID attemptId;
    private ExamAttemptStatus status;
    private Double score;
    private Boolean passed;
    private String message;
    private List<StudentExamQuestionResultResponse> questions;
}
