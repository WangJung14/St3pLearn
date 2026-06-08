package com.tommy.payment.application.service;

import com.tommy.payment.application.port.in.GetSampleUseCase;
import com.tommy.payment.application.port.out.SampleRepository;
import com.tommy.payment.domain.entity.SamplePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSampleService implements GetSampleUseCase {

    private final SampleRepository sampleRepository;

    @Override
    public SamplePayment getSampleById(Long id) {
        return sampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sample not found with id: " + id));
    }
}
