package com.tommy.catalog.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private UUID id;
    private UUID lessonId;
    private UUID userId;
    private String userFullName;
    private String userRole;
    private String content;
    private UUID parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> replies;
}
