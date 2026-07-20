package com.tommy.admin.infrastructure.persistence.repository;

import com.tommy.admin.domain.entity.ModerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModerationLogRepository extends JpaRepository<ModerationLog, UUID> {
    List<ModerationLog> findByCaseIdOrderByCreatedAtDesc(UUID caseId);
}
