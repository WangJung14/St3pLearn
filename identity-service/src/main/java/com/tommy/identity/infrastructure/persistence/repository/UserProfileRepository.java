package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    // Find profile by public_id
    Optional<UserProfile> findByPublicId(String publicId);
}
