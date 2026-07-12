package com.tommy.learning.application.dto.response;

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
public class QuestionBankResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private UUID instructorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
