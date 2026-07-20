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
public class SystemAuditEvent {
    private UUID actorId; // Admin/Moderator ID
    private String action; // e.g. "ASSIGN_ROLE", "UPDATE_COMMISSION"
    private String targetType; // e.g. "USER", "SYSTEM_CONFIG"
    private String targetId; // ID of the target
    private String oldValue;
    private String newValue;
    private String description;
    private LocalDateTime timestamp;
}
