package com.tommy.identity.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginHistoryResponse {
    private UUID logId;
    private String eventType;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}
