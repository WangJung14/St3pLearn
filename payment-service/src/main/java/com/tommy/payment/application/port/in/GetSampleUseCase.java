package com.tommy.payment.application.port.in;

import com.tommy.payment.domain.entity.SamplePayment;

public interface GetSampleUseCase {
    SamplePayment getSampleById(Long id);
}
