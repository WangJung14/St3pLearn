package com.tommy.learning.application.dto.response;


import com.tommy.learning.domain.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EnrollmentResponse {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private EnrollmentStatus status;
    private BigDecimal progressPercent;
    private LocalDateTime enrolledAt;
}
