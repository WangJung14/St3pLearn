package com.tommy.catalog.application.dto.request;

import lombok.Data;

@Data
public class ReviewDocumentRequest {
    private boolean approved;
    private String rejectReason;
}
