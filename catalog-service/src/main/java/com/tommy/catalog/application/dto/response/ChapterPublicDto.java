package com.tommy.catalog.application.dto.response;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterPublicDto {
    private UUID id;
    private String title;
    private int orderIndex;

    private List<LessonPublicDto> lessons;
}
