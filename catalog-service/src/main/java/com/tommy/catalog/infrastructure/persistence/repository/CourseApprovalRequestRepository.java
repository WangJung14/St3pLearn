package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.application.dto.response.CourseApprovalResponse;
import com.tommy.catalog.domain.entity.CourseApprovalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CourseApprovalRequestRepository extends JpaRepository<CourseApprovalRequest, UUID> {
    @Query("SELECT new com.tommy.catalog.application.dto.response.CourseApprovalResponse(" +
            "req.id, req.courseId, c.title, req.submittedBy, req.status, req.submittedAt) " +
            "FROM CourseApprovalRequest req " +
            "JOIN Course c ON req.courseId = c.id " +
            "WHERE req.status = :status")
    Page<CourseApprovalResponse> findApprovalsByStatus(@Param("status") String status, Pageable pageable);

    // Anti multi submit
    boolean existsByCourseIdAndStatus(UUID courseId, String status);

    // Find ticket by status
    Optional<CourseApprovalRequest> findByCourseIdAndStatus(UUID courseId, String status);
}
