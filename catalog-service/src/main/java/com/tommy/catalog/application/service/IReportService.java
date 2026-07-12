package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.CreateReportRequest;
import com.tommy.catalog.application.dto.request.ProcessReportRequest;
import com.tommy.catalog.application.dto.response.ReportTicketResponse;
import com.tommy.catalog.domain.enums.ReportStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IReportService {
    ReportTicketResponse createReport(UUID reporterId, CreateReportRequest request);

    Page<ReportTicketResponse> getReports(ReportStatus status, int page, int size);

    ReportTicketResponse processReport(UUID reportId, ProcessReportRequest request);
}
