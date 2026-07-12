package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.CreateReportRequest;
import com.tommy.catalog.application.dto.request.ProcessReportRequest;
import com.tommy.catalog.application.dto.response.ReportTicketResponse;
import com.tommy.catalog.application.service.IReportService;
import com.tommy.catalog.domain.entity.ReportTicket;
import com.tommy.catalog.domain.enums.ReportStatus;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.catalog.infrastructure.persistence.repository.ReportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService implements IReportService {

    private final ReportTicketRepository reportTicketRepository;

    @Override
    @Transactional
    public ReportTicketResponse createReport(UUID reporterId, CreateReportRequest request) {
        ReportTicket ticket = ReportTicket.builder()
                .reporterId(reporterId)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .description(request.getDescription())
                .status(ReportStatus.PENDING)
                .build();

        ticket = reportTicketRepository.save(ticket);
        log.info("Created report ticket {} by user {}", ticket.getId(), reporterId);

        return mapToResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportTicketResponse> getReports(ReportStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportTicket> tickets;

        if (status != null) {
            tickets = reportTicketRepository.findAllByStatus(status, pageable);
        } else {
            tickets = reportTicketRepository.findAll(pageable);
        }

        return tickets.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ReportTicketResponse processReport(UUID reportId, ProcessReportRequest request) {
        ReportTicket ticket = reportTicketRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        ticket.setStatus(request.getStatus());
        ticket.setAdminNotes(request.getAdminNotes());

        ticket = reportTicketRepository.save(ticket);
        log.info("Processed report ticket {} with status {}", reportId, request.getStatus());

        return mapToResponse(ticket);
    }

    private ReportTicketResponse mapToResponse(ReportTicket ticket) {
        return ReportTicketResponse.builder()
                .id(ticket.getId())
                .reporterId(ticket.getReporterId())
                .targetType(ticket.getTargetType())
                .targetId(ticket.getTargetId())
                .reason(ticket.getReason())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .adminNotes(ticket.getAdminNotes())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
