package com.tommy.identity.application.service;

import com.tommy.identity.application.port.in.GetSampleUseCase;
import com.tommy.identity.application.port.out.SampleRepository;
import com.tommy.identity.domain.entity.SampleIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSampleService implements GetSampleUseCase {

    private final SampleRepository sampleRepository;

    @Override
    public SampleIdentity getSampleById(Long id) {
        return sampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sample not found with id: " + id));
    }
}
