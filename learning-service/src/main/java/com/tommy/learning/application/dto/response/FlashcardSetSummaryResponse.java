package com.tommy.learning.application.dto.response;

import com.tommy.learning.domain.enums.VisibilityEnum;
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
public class FlashcardSetSummaryResponse {
    private UUID id;
    private String title;
    private UUID courseId;
    private UUID instructorId;
    private VisibilityEnum visibility;
    private long cardCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
