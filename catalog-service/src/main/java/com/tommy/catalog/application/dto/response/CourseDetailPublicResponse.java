package com.tommy.catalog.application.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDetailPublicResponse {
    private UUID id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private String level;
    private UUID instructorId;

    private List<ChapterPublicDto> curriculum;
}