package com.tommy.catalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessApprovalRequest {
    @NotBlank(message = "Action cannot be blank (APPROVE/REJECT)")
    private String action;
    private String reviewNote;
}
