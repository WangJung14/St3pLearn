package com.tommy.payment.infrastructure.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CatalogCourseResponse {
    private UUID id;
    private BigDecimal price;
    private String status;
}
