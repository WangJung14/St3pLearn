package com.tommy.catalog.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CourseSummaryResponse(UUID id,
                                   String title,
                                   String slug,
                                   String thumbnailUrl,
                                   BigDecimal price,
                                   UUID instructorId) {
}
