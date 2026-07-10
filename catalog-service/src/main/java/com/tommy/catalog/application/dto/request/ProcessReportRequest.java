package com.tommy.catalog.application.dto.request;

import com.tommy.catalog.domain.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessReportRequest {
    @NotNull(message = "Status is required")
    private ReportStatus status;

    private String adminNotes;
}
