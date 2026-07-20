package com.tommy.payment.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutResponse {
    private String orderNumber;
    private String paymentUrl;
}
