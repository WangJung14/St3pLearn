package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.infrastructure.persistence.entity.SampleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleJpaRepository extends JpaRepository<SampleJpaEntity, Long> {
}
