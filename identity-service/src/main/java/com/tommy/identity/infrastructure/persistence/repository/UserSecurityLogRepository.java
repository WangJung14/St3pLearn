package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.domain.entity.UserSecurityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface UserSecurityLogRepository extends JpaRepository<UserSecurityLog, UUID> {

    /**
     * Find user history log, sort by time
     * */
    Page<UserSecurityLog> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
