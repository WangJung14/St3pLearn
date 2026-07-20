package com.tommy.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationResolvedEvent {
    private UUID caseId;
    private UUID adminId;
    private String targetType;
    private String targetId;
    private String action; // e.g., SUSPEND_USER, HIDE_COURSE, IGNORE
    private String reason;
}
