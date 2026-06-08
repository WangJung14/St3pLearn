package com.tommy.learning.application.service;

import com.tommy.learning.application.port.in.GetSampleUseCase;
import com.tommy.learning.application.port.out.SampleRepository;
import com.tommy.learning.domain.entity.SampleLearning;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSampleService implements GetSampleUseCase {

    private final SampleRepository sampleRepository;

    @Override
    public SampleLearning getSampleById(Long id) {
        return sampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sample not found with id: " + id));
    }
}
