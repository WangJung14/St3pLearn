package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class LessonContentRequest {
    @NotBlank(message = "Content type cannot be blank")
    private String contentType;

    @NotBlank(message = "Storage url cannot be blank")
    private String storageUrl;

    private Long fileSize;

    private String checksum;

    private Map<String, Object> metadata; // Save json
}
