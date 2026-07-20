package com.tommy.admin.infrastructure.persistence.repository;

import com.tommy.admin.domain.entity.ModerationCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ModerationCaseRepository extends JpaRepository<ModerationCase, UUID> {
}
