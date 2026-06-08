package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.domain.entity.UserSecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface UserSecurityLogRepository extends JpaRepository<UserSecurityLog, UUID> {
}
