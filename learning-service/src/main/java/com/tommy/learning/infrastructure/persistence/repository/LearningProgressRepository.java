package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.Optional;

import java.util.UUID;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lp FROM LearningProgress lp WHERE lp.enrollmentId = :enrollmentId")
    Optional<LearningProgress> findByEnrollmentIdWithLock(@Param("enrollmentId") UUID enrollmentId);
}
