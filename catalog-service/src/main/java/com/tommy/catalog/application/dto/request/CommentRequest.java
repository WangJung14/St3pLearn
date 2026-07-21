package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    @NotBlank(message = "Comment content is required")
    private String content;

    @NotBlank(message = "User full name is required")
    private String userFullName;

    private UUID parentCommentId;
}
