package com.tommy.catalog.application.dto.response;

import com.tommy.catalog.domain.entity.Course;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApprovalDetailResponse {
    private UUID approvalRequestId;
    private String ticketStatus;
    private UUID submittedBy;
    private LocalDateTime submittedAt;

    private Course courseInfo;

    private int totalLessons;
}
