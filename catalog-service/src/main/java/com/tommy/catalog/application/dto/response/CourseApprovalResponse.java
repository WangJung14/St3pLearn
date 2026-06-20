package com.tommy.catalog.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApprovalResponse {
    private UUID approvalRequestId;
    private UUID courseId;
    private String courseTitle;
    private UUID instructorId;
    private String status;
    private LocalDateTime submittedAt;
}
