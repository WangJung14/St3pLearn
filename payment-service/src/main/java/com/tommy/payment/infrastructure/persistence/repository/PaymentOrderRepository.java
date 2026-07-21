package com.tommy.payment.infrastructure.persistence.repository;

import com.tommy.payment.domain.entity.PaymentOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, UUID> {
    Optional<PaymentOrder> findByOrderNumber(String orderNumber);

    Page<PaymentOrder> findByStudentIdOrderByCreatedAtDesc(UUID studentId, Pageable pageable);
}
