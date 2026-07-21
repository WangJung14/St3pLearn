package com.tommy.catalog.application.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonPublicDto {
    private UUID id;
    private String title;
    private int orderIndex;
    private Integer duration;
    private boolean isPreview;

    private String videoUrl;
    private String contentType;
    private String textContent;
}