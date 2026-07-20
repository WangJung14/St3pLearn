package com.tommy.payment.infrastructure.persistence.repository;

import com.tommy.payment.domain.entity.PaymentGatewayLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentGatewayLogRepository extends JpaRepository<PaymentGatewayLog, UUID> {
}
