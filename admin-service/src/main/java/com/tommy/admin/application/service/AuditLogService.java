package com.tommy.admin.application.service;

import com.tommy.admin.domain.entity.AuditLog;
import com.tommy.admin.infrastructure.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    @Transactional
    public void saveLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}
