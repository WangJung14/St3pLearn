package com.tommy.admin.application.service;

import com.tommy.admin.domain.entity.ModerationCase;
import com.tommy.admin.domain.entity.ModerationLog;
import com.tommy.admin.infrastructure.persistence.repository.ModerationCaseRepository;
import com.tommy.admin.infrastructure.persistence.repository.ModerationLogRepository;
import com.tommy.common.event.ModerationResolvedEvent;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationService {

    private final ModerationCaseRepository caseRepository;
    private final ModerationLogRepository logRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional(readOnly = true)
    public List<ModerationCase> getAllCases() {
        return caseRepository.findAll();
    }

    @Transactional
    public ModerationCase openCaseManual(UUID reportId, UUID reporterId, String targetType, String targetId, String reason, String description) {
        ModerationCase modCase = ModerationCase.builder()
                .reportId(reportId)
                .reporterId(reporterId)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .description(description)
                .status("OPEN")
                .build();
        return caseRepository.save(modCase);
    }

    @Transactional
    public ModerationCase dismissCase(UUID caseId, UUID adminId, String notes) {
        ModerationCase modCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new AppException(ErrorCode.MODERATION_CASE_NOT_FOUND));

        modCase.setStatus("DISMISSED");
        modCase.setAssignedAdminId(adminId);
        caseRepository.save(modCase);

        ModerationLog logEntry = ModerationLog.builder()
                .caseId(caseId)
                .adminId(adminId)
                .actionTaken("DISMISSED")
                .notes(notes)
                .build();
        logRepository.save(logEntry);

        return modCase;
    }

    @Transactional
    public ModerationCase resolveCase(UUID caseId, UUID adminId, String action, String notes) {
        ModerationCase modCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new AppException(ErrorCode.MODERATION_CASE_NOT_FOUND));

        modCase.setStatus("RESOLVED");
        modCase.setAssignedAdminId(adminId);
        caseRepository.save(modCase);

        ModerationLog logEntry = ModerationLog.builder()
                .caseId(caseId)
                .adminId(adminId)
                .actionTaken(action)
                .notes(notes)
                .build();
        logRepository.save(logEntry);

        // Emit Event so other services can execute the action (e.g. SUSPEND_USER, HIDE_COURSE)
        ModerationResolvedEvent event = ModerationResolvedEvent.builder()
                .caseId(caseId)
                .adminId(adminId)
                .targetType(modCase.getTargetType())
                .targetId(modCase.getTargetId())
                .action(action)
                .reason(notes)
                .build();

        rabbitTemplate.convertAndSend("admin.events.exchange", "moderation.resolved", event);
        log.info("Emitted ModerationResolvedEvent for case {} with action {}", caseId, action);

        return modCase;
    }

    @Transactional(readOnly = true)
    public List<ModerationLog> getLogsForCase(UUID caseId) {
        return logRepository.findByCaseIdOrderByCreatedAtDesc(caseId);
    }
}
