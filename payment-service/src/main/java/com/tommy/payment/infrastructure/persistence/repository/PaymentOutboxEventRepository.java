package com.tommy.payment.infrastructure.persistence.repository;

import com.tommy.payment.domain.entity.PaymentOutboxEvent;
import com.tommy.payment.domain.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentOutboxEventRepository extends JpaRepository<PaymentOutboxEvent, UUID> {
    List<PaymentOutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
