package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // Find all unrevoked Refresh Token for a user
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(UUID userId);
}
