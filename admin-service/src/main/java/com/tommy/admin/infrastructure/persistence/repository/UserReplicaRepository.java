package com.tommy.admin.infrastructure.persistence.repository;

import com.tommy.admin.domain.entity.UserReplica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserReplicaRepository extends JpaRepository<UserReplica, UUID> {
    long countByRole(String role);
}
