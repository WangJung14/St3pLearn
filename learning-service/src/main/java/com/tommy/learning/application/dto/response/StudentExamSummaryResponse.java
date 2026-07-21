package com.tommy.learning.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExamSummaryResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private Integer durationMinutes;
    private Double passingScore;
    private Integer maxAttempts;
    private long attemptsUsed;
    private long remainingAttempts;
    private long questionCount;
    private boolean passed;
    private boolean canStart;
}
