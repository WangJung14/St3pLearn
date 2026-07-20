package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    Optional<Certificate> findByStudentIdAndCourseIdAndIsDeletedFalse(UUID studentId, UUID courseId);
    Optional<Certificate> findByCertificateCodeAndIsDeletedFalse(String certificateCode);
    Optional<Certificate> findByIdAndStudentIdAndIsDeletedFalse(UUID id, UUID studentId);
}
