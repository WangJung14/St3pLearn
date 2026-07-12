package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.CourseReplica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseReplicaRepository extends JpaRepository<CourseReplica, UUID> {
}
