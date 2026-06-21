package com.tommy.catalog.application.dto.response;


import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCardResponse {
    private UUID id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private BigDecimal price;
    private String level;
    private UUID instructorId;
    private BigDecimal avgRating;
    private Integer totalReviews;
}
