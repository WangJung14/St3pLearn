package com.tommy.learning.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VerifyCertificateResponse {
    private boolean isValid;
    private String message;
    private UUID studentId;
    private String studentName;
    private UUID courseId;
    private String courseName;
    private LocalDateTime issueDate;
}
