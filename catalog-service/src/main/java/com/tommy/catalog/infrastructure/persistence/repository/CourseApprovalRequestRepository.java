package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.CourseApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseApprovalRequestRepository extends JpaRepository<CourseApprovalRequest, UUID> {
}
