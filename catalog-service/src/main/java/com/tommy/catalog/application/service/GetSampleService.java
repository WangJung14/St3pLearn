package com.tommy.catalog.application.service;

import com.tommy.catalog.application.port.in.GetSampleUseCase;
import com.tommy.catalog.application.port.out.SampleRepository;
import com.tommy.catalog.domain.entity.SampleCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSampleService implements GetSampleUseCase {

    private final SampleRepository sampleRepository;

    @Override
    public SampleCatalog getSampleById(Long id) {
        return sampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sample not found with id: " + id));
    }
}
