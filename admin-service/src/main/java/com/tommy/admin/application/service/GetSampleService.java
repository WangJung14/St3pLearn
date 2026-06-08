package com.tommy.admin.application.service;

import com.tommy.admin.application.port.in.GetSampleUseCase;
import com.tommy.admin.application.port.out.SampleRepository;
import com.tommy.admin.domain.entity.SampleAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSampleService implements GetSampleUseCase {

    private final SampleRepository sampleRepository;

    @Override
    public SampleAdmin getSampleById(Long id) {
        return sampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sample not found with id: " + id));
    }
}
