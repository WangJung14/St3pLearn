package com.tommy.catalog.application.dto.response;

import com.tommy.catalog.domain.enums.ReportStatus;
import com.tommy.catalog.domain.enums.ReportTargetType;
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
public class ReportTicketResponse {
    private UUID id;
    private UUID reporterId;
    private ReportTargetType targetType;
    private String targetId;
    private String reason;
    private String description;
    private ReportStatus status;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
