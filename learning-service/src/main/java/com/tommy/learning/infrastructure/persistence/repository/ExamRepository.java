package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.Exam;
import com.tommy.learning.domain.enums.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    
    List<Exam> findByInstructorIdAndIsDeletedFalse(UUID instructorId);
    
    List<Exam> findByCourseIdAndIsDeletedFalse(UUID courseId);
    
    Optional<Exam> findByIdAndIsDeletedFalse(UUID id);

    @Query("""
            SELECT exam
            FROM Exam exam
            WHERE exam.isDeleted = false
              AND exam.status = :status
              AND EXISTS (
                  SELECT enrollment.id
                  FROM Enrollment enrollment
                  WHERE enrollment.studentId = :studentId
                    AND enrollment.courseId = exam.courseId
              )
            ORDER BY exam.createdAt DESC
            """)
    List<Exam> findAvailableForStudent(
            @Param("studentId") UUID studentId,
            @Param("status") ExamStatus status);
}
