package com.tommy.learning.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CertificateResponse {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private String certificateCode;
    private LocalDateTime issueDate;
}
