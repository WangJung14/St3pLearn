package com.tommy.identity.infrastructure.persistence.repository;

import com.tommy.identity.domain.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // search users for admin
    @Query("SELECT a FROM Account a LEFT JOIN a.profile p WHERE " +
           "LOWER(a.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Account> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
