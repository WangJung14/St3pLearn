package com.tommy.learning.application.dto.response;

import com.tommy.learning.domain.enums.ExamStatus;
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
public class ExamResponse {
    private UUID id;
    private UUID courseId;
    private UUID instructorId;
    private String title;
    private Integer durationMinutes;
    private Double passingScore;
    private Double totalScore;
    private Integer maxAttempts;
    private ExamStatus status;
    private List<QuestionResponse> questions;
}
