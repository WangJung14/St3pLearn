package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.ReportTicket;
import com.tommy.catalog.domain.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportTicketRepository extends JpaRepository<ReportTicket, UUID> {
    Page<ReportTicket> findAllByStatus(ReportStatus status, Pageable pageable);
}
