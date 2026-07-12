package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.domain.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {
}
