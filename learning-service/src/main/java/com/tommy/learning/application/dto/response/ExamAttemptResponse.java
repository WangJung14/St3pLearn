package com.tommy.learning.application.dto.response;

import com.tommy.learning.domain.enums.ExamAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttemptResponse {
    private UUID id;
    private UUID examId;
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Double score;
    private Boolean passed;
    private ExamAttemptStatus status;
}
