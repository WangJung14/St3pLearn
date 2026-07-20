package com.tommy.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSubmittedEvent {
    private UUID reportId;
    private UUID reporterId;
    private String targetType; // COURSE, COMMENT, USER, etc.
    private String targetId;
    private String reason;
    private String description;
    private LocalDateTime createdAt;
}
