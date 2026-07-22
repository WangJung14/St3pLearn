package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.CourseDocument;
import com.tommy.catalog.domain.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseDocumentRepository extends JpaRepository<CourseDocument, UUID> {
    List<CourseDocument> findByCourseId(UUID courseId);
    List<CourseDocument> findByStatus(DocumentStatus status);
    List<CourseDocument> findByUploadedBy(UUID uploadedBy);
}
