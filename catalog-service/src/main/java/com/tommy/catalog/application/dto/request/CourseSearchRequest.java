package com.tommy.catalog.application.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CourseSearchRequest {
    private String keyword;
    private String level;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private UUID categoryId;

    private int page = 0;
    private int size = 10;

    private String sortBy = "createdAt";
    private String sortDir = "DESC";
}
