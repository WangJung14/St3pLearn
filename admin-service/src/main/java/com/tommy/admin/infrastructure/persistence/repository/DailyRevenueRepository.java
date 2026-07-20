package com.tommy.admin.infrastructure.persistence.repository;

import com.tommy.admin.domain.entity.DailyRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyRevenueRepository extends JpaRepository<DailyRevenue, UUID> {
    Optional<DailyRevenue> findByDate(LocalDate date);
}
