package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    // login
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUsername(String username);

    // register validate
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
