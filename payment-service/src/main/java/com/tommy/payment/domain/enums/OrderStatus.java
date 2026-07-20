package com.tommy.payment.domain.enums;

public enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,
    PAID,
    FAILED,
    CANCELLED,
    REFUNDED
}
