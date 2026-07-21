package com.tommy.catalog.application.dto.request;

import lombok.Data;

@Data
public class DocumentCallbackRequest {
    private String status;
    private Integer chunkCount;
    private String error;
}
