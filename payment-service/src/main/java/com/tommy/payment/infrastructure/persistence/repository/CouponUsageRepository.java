package com.tommy.payment.infrastructure.persistence.repository;

import com.tommy.payment.domain.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {
    boolean existsByCouponIdAndStudentId(UUID couponId, UUID studentId);
}
