package com.tommy.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon_usages", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"coupon_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "payment_order_id", nullable = false)
    private UUID paymentOrderId;

    @Column(name = "used_at")
    @Builder.Default
    private LocalDateTime usedAt = LocalDateTime.now();
}
