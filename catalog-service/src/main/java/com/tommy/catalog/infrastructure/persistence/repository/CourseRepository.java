package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> , JpaSpecificationExecutor<Course> {
    boolean existsBySlug(String slug);
}
